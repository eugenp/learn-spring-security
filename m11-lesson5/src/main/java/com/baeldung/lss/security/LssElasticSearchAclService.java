package com.baeldung.lss.security;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;
import org.springframework.security.util.FieldUtils;
import org.springframework.util.Assert;

import com.baeldung.lss.model.acl.AclClass;
import com.baeldung.lss.model.acl.AclEntry;
import com.baeldung.lss.model.acl.AclObjectIdentity;
import com.baeldung.lss.model.acl.AclSid;
import com.baeldung.lss.persistence.acl.AclClassRepository;
import com.baeldung.lss.persistence.acl.AclEntryRepository;
import com.baeldung.lss.persistence.acl.AclObjectIdentityRepository;
import com.baeldung.lss.persistence.acl.AclSidRepository;
import com.google.common.collect.Lists;

public class LssElasticSearchAclService implements AclService {
    @Autowired
    private AclObjectIdentityRepository aclObjIdRepository;

    @Autowired
    private AclClassRepository aclClassRepository;

    @Autowired
    private AclSidRepository aclSidRepository;

    @Autowired
    private AclEntryRepository aclEntryRepository;

    protected AclCache aclCache;

    protected PermissionGrantingStrategy grantingStrategy;

    protected AclAuthorizationStrategy aclAuthorizationStrategy;

    private PermissionFactory permissionFactory = new DefaultPermissionFactory();

    private final Field fieldAces = FieldUtils.getField(AclImpl.class, "aces");
    private final Field fieldAcl = FieldUtils.getField(AccessControlEntryImpl.class, "acl");

    public LssElasticSearchAclService(AclCache aclCache, PermissionGrantingStrategy grantingStrategy, AclAuthorizationStrategy aclAuthorizationStrategy) {
        super();
        this.aclCache = aclCache;
        this.grantingStrategy = grantingStrategy;
        this.aclAuthorizationStrategy = aclAuthorizationStrategy;
        //
        this.fieldAces.setAccessible(true);
        this.fieldAcl.setAccessible(true);
    }

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        Assert.notNull(parentIdentity, "Parent Identity required");
        final String parentPK = retrieveObjectIdentityPrimaryKey(parentIdentity);
        final List<AclObjectIdentity> children = aclObjIdRepository.findByParentObjectId(parentPK);
        if (children.size() == 0) {
            return null;
        }
        final List<ObjectIdentity> result = new ArrayList<ObjectIdentity>();
        children.forEach(child -> result.add(new ObjectIdentityImpl(parentIdentity.getType(), child.getObjectIdIdentity())));
        return result;
    }

    @Override
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        return readAclById(object, null);
    }

    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        final Map<ObjectIdentity, Acl> map = readAclsById(Arrays.asList(object), sids);
        Assert.isTrue(map.containsKey(object), "There should have been an Acl entry for ObjectIdentity " + object);
        return map.get(object);
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        return readAclsById(objects, null);
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        final Map<ObjectIdentity, Acl> result = lookupAclsById(objects, sids);
        for (final ObjectIdentity oid : objects) {
            if (!result.containsKey(oid)) {
                throw new NotFoundException("Unable to find ACL information for object identity '" + oid + "'");
            }
        }
        return result;
    }

    // util

    protected String retrieveObjectIdentityPrimaryKey(ObjectIdentity objectIdentity) {
        final AclClass aclClass = aclClassRepository.findOneByClassName(objectIdentity.getType());
        if (aclClass != null) {
            final AclObjectIdentity aoi = aclObjIdRepository.findOneByObjectIdIdentityAndObjectIdClass(objectIdentity.getIdentifier()
                .toString(), aclClass.getId());
            if (aoi != null) {
                return aoi.getId();
            }
        }
        return null;
    }

    // lookup

    private Map<ObjectIdentity, Acl> lookupAclsById(List<ObjectIdentity> objects, List<Sid> sids) {
        Assert.notEmpty(objects, "Objects to lookup required");
        Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
        final Set<ObjectIdentity> toLoad = new HashSet<ObjectIdentity>();

        for (int i = 0; i < objects.size(); i++) {
            final ObjectIdentity oid = objects.get(i);
            boolean aclFound = false;
            final Acl acl = aclCache.getFromCache(oid);
            if (acl != null) {
                if (acl.isSidLoaded(sids)) {
                    result.put(acl.getObjectIdentity(), acl);
                    aclFound = true;
                } else {
                    throw new IllegalStateException("Error: SID-filtered element detected when implementation does not perform SID filtering " + "- have you added something to the cache manually?");
                }
            }
            if (!aclFound) {
                toLoad.add(oid);
            }
        }
        if (toLoad.size() > 0) {
            result = lookupObjectIdentities(toLoad, sids);
            for (final Acl loadedAcl : result.values()) {
                aclCache.putInCache((AclImpl) loadedAcl);
            }
        }
        return result;
    }

    private Map<ObjectIdentity, Acl> lookupObjectIdentities(Set<ObjectIdentity> objectIdentities, List<Sid> sids) {
        Assert.notEmpty(objectIdentities, "Must provide identities to lookup");
        final Map<Serializable, Acl> acls = new HashMap<Serializable, Acl>();

        final List<AclObjectIdentity> aoiList = new ArrayList<AclObjectIdentity>();
        for (final ObjectIdentity objectIdentity : objectIdentities) {
            final AclClass aclClass = aclClassRepository.findOneByClassName(objectIdentity.getType());
            if (aclClass != null) {
                AclObjectIdentity aclObjId = aclObjIdRepository.findOneByObjectIdIdentityAndObjectIdClass(objectIdentity.getIdentifier()
                    .toString(), aclClass.getId());
                if (aclObjId != null) {
                    aoiList.add(aclObjId);
                }
            }
        }
        final Set<String> parentAclIds = getParentIdsToLookup(acls, aoiList, sids);
        if (parentAclIds.size() > 0) {
            lookUpParentAcls(acls, parentAclIds, sids);
        }

        final Map<ObjectIdentity, Acl> resultMap = new HashMap<ObjectIdentity, Acl>();
        for (final Acl inputAcl : acls.values()) {
            Assert.isInstanceOf(AclImpl.class, inputAcl, "Map should have contained an AclImpl");
            Assert.isInstanceOf(String.class, ((AclImpl) inputAcl).getId(), "Acl.getId() must be String");

            final Acl result = convert(acls, ((AclImpl) inputAcl).getId()
                .toString());
            resultMap.put(result.getObjectIdentity(), result);
        }
        return resultMap;
    }

    private AclImpl convert(Map<Serializable, Acl> inputMap, String currentIdentity) {
        Assert.notEmpty(inputMap, "InputMap required");
        Assert.notNull(currentIdentity, "CurrentIdentity required");
        final Acl uncastAcl = inputMap.get(currentIdentity);
        Assert.isInstanceOf(AclImpl.class, uncastAcl, "The inputMap contained a non-AclImpl");

        final AclImpl inputAcl = (AclImpl) uncastAcl;
        Acl parent = inputAcl.getParentAcl();
        if ((parent != null) && (parent instanceof StubAclParent)) {
            final StubAclParent stubAclParent = (StubAclParent) parent;
            parent = convert(inputMap, stubAclParent.getId());
        }

        final AclImpl result = new AclImpl(inputAcl.getObjectIdentity(), inputAcl.getId(), aclAuthorizationStrategy, grantingStrategy, parent, null, inputAcl.isEntriesInheriting(), inputAcl.getOwner());
        final List<AccessControlEntryImpl> aces = readAces(inputAcl);
        final List<AccessControlEntryImpl> acesNew = new ArrayList<AccessControlEntryImpl>();
        for (final AccessControlEntryImpl ace : aces) {
            setAclOnAce(ace, result);
            acesNew.add(ace);
        }
        setAces(result, acesNew);

        return result;
    }

    private void setAces(AclImpl acl, List<AccessControlEntryImpl> aces) {
        try {
            fieldAces.set(acl, aces);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not set AclImpl entries", e);
        }
    }

    private void setAclOnAce(AccessControlEntryImpl ace, AclImpl acl) {
        try {
            fieldAcl.set(ace, acl);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not or set AclImpl on AccessControlEntryImpl fields", e);
        }
    }

    private Set<String> getParentIdsToLookup(Map<Serializable, Acl> acls, List<AclObjectIdentity> aoiList, List<Sid> sids) {
        new ArrayList<String>();
        final Set<String> parentIdsToLookup = new HashSet<String>();
        final List<AclEntry> entries = new ArrayList<AclEntry>();
        for (final AclObjectIdentity aoi : aoiList) {
            entries.addAll(aclEntryRepository.findByObjectIdentityId(aoi.getId()));
            final String parentId = aoi.getParentObjectId();
            if (parentId != null) {
                if (!acls.containsKey(parentId)) {
                    final MutableAcl cached = aclCache.getFromCache(parentId);

                    if ((cached == null) || !cached.isSidLoaded(sids)) {
                        parentIdsToLookup.add(parentId);
                    } else {

                        acls.put(cached.getId(), cached);
                    }

                    parentIdsToLookup.add(aoi.getParentObjectId());
                }
            }
        }

        for (final AclObjectIdentity aoi : aoiList) {
            convertObjectIdentityIntoAclObject(acls, aoi, entries);
        }
        return parentIdsToLookup;
    }

    private void convertObjectIdentityIntoAclObject(Map<Serializable, Acl> acls, AclObjectIdentity objectIdentity, List<AclEntry> aclEntries) {
        final String id = objectIdentity.getId();
        Acl acl = acls.get(id);
        if (acl == null) {
            final String objectClassName = aclClassRepository.findOne(objectIdentity.getObjectIdClass())
                .getClassName();
            final ObjectIdentity springOid = new ObjectIdentityImpl(objectClassName, objectIdentity.getObjectIdIdentity());
            Acl parentAcl = null;
            if (objectIdentity.getParentObjectId() != null) {
                parentAcl = new StubAclParent(objectIdentity.getParentObjectId());
            }

            Sid owner;
            final AclSid aclSid = aclSidRepository.findOne(objectIdentity.getOwnerId());
            if (aclSid.isPrincipal()) {
                owner = new PrincipalSid(aclSid.getSid());
            } else {
                owner = new GrantedAuthoritySid(aclSid.getSid());
            }

            acl = new AclImpl(springOid, id, aclAuthorizationStrategy, grantingStrategy, parentAcl, null, objectIdentity.isEntriesInheriting(), owner);
            acls.put(id, acl);
        }

        final List<AclEntry> belongedEntries = findAclEntryOfObjectIdentity(objectIdentity, aclEntries);
        for (final AclEntry entry : belongedEntries) {
            if (entry.getSid() != null) {
                final AccessControlEntryImpl ace = convertAclEntryIntoObject(acl, entry);
                final List<AccessControlEntryImpl> aces = readAces((AclImpl) acl);
                if (!aces.contains(ace)) {
                    aces.add(ace);
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private List<AccessControlEntryImpl> readAces(AclImpl acl) {
        try {
            return (List<AccessControlEntryImpl>) fieldAces.get(acl);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not obtain AclImpl.aces field", e);
        }
    }

    private AccessControlEntryImpl convertAclEntryIntoObject(Acl acl, AclEntry aclEntry) {
        String aceId = aclEntry.getId();
        Sid recipient = createSidFromMongoId(aclEntry.getSid());

        int mask = aclEntry.getMask();
        Permission permission = permissionFactory.buildFromMask(mask);
        boolean granting = aclEntry.isGranting();
        boolean auditSuccess = aclEntry.isAuditSuccess();
        boolean auditFailure = aclEntry.isAuditFailure();

        return new AccessControlEntryImpl(aceId, acl, recipient, permission, granting, auditSuccess, auditFailure);
    }

    private Sid createSidFromMongoId(String id) {
        Sid sid;
        AclSid aclSid = aclSidRepository.findOne(id);
        if (aclSid.isPrincipal()) {
            sid = new PrincipalSid(aclSid.getSid());
        } else {
            sid = new GrantedAuthoritySid(aclSid.getSid());
        }
        return sid;
    }

    private List<AclEntry> findAclEntryOfObjectIdentity(AclObjectIdentity objectIdentity, List<AclEntry> aclEntries) {
        final List<AclEntry> result = new ArrayList<AclEntry>();
        for (final AclEntry entry : aclEntries) {
            if (entry.getObjectIdentityId()
                .equals(objectIdentity.getId())) {
                result.add(entry);
            }
        }
        Collections.sort(result, new Comparator<AclEntry>() {
            @Override
            public int compare(AclEntry o1, AclEntry o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        return result;
    }

    private void lookUpParentAcls(Map<Serializable, Acl> acls, Set<String> parentAclIds, List<Sid> sids) {
        final List<AclObjectIdentity> aoiList = Lists.newArrayList(aclObjIdRepository.findAll(parentAclIds));
        final Set<String> parentIdsToLookup = getParentIdsToLookup(acls, aoiList, sids);
        if ((parentIdsToLookup != null) && (parentIdsToLookup.size() > 0)) {
            lookUpParentAcls(acls, parentIdsToLookup, sids);
        }

    }

    // ====

    @SuppressWarnings("serial")
    private static class StubAclParent implements Acl {
        private final String id;

        public StubAclParent(String id) {
            this.id = id;
        }

        @Override
        public List<AccessControlEntry> getEntries() {
            throw new UnsupportedOperationException("Stub only");
        }

        public String getId() {
            return id;
        }

        @Override
        public ObjectIdentity getObjectIdentity() {
            throw new UnsupportedOperationException("Stub only");
        }

        @Override
        public Sid getOwner() {
            throw new UnsupportedOperationException("Stub only");
        }

        @Override
        public Acl getParentAcl() {
            throw new UnsupportedOperationException("Stub only");
        }

        @Override
        public boolean isEntriesInheriting() {
            throw new UnsupportedOperationException("Stub only");
        }

        @Override
        public boolean isGranted(List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException, UnloadedSidException {
            throw new UnsupportedOperationException("Stub only");
        }

        @Override
        public boolean isSidLoaded(List<Sid> sids) {
            throw new UnsupportedOperationException("Stub only");
        }
    }

}
