package com.baeldung.lss.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.baeldung.lss.model.acl.AclClass;
import com.baeldung.lss.model.acl.AclEntry;
import com.baeldung.lss.model.acl.AclObjectIdentity;
import com.baeldung.lss.model.acl.AclSid;
import com.baeldung.lss.persistence.acl.AclClassRepository;
import com.baeldung.lss.persistence.acl.AclEntryRepository;
import com.baeldung.lss.persistence.acl.AclObjectIdentityRepository;
import com.baeldung.lss.persistence.acl.AclSidRepository;

public class LssElasticSearchMutableAclService extends LssElasticSearchAclService implements MutableAclService {

    public LssElasticSearchMutableAclService(AclCache aclCache, PermissionGrantingStrategy grantingStrategy, AclAuthorizationStrategy aclAuthorizationStrategy) {
        super(aclCache, grantingStrategy, aclAuthorizationStrategy);
    }

    @Autowired
    private AclObjectIdentityRepository aclObjIdRepository;

    @Autowired
    private AclClassRepository aclClassRepository;

    @Autowired
    private AclSidRepository aclSidRepository;

    @Autowired
    private AclEntryRepository aclEntryRepository;

    @Override
    public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
        Assert.notNull(objectIdentity, "Object Identity required");
        if (retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
            throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
        }
        final Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
        final PrincipalSid sid = new PrincipalSid(auth);
        createObjectIdentity(objectIdentity, sid);
        final Acl acl = readAclById(objectIdentity);
        Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should be been returned");
        return (MutableAcl) acl;
    }

    @Override
    public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
        Assert.notNull(objectIdentity, "Object Identity required");
        Assert.notNull(objectIdentity.getIdentifier(), "Object Identity doesn't provide an identifier");

        final List<ObjectIdentity> children = findChildren(objectIdentity);
        if (children != null) {
            if (deleteChildren) {
                for (final ObjectIdentity child : children) {
                    deleteAcl(child, true);
                }
            } else {
                throw new ChildrenExistException("Cannot delete '" + objectIdentity + "' (has " + children.size() + " children)");
            }
        }

        final String objIdPrimaryKey = retrieveObjectIdentityPrimaryKey(objectIdentity);
        aclEntryRepository.deleteByObjectIdentityId(objIdPrimaryKey);
        aclObjIdRepository.delete(objIdPrimaryKey);
        aclCache.evictFromCache(objectIdentity);
    }

    @Override
    public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
        Assert.notNull(acl.getId(), "Object Identity doesn't provide an identifier");
        aclEntryRepository.deleteByObjectIdentityId(retrieveObjectIdentityPrimaryKey(acl.getObjectIdentity()));
        createEntries(acl);
        updateObjectIdentity(acl);
        clearCacheIncludingChildren(acl.getObjectIdentity());
        return (MutableAcl) super.readAclById(acl.getObjectIdentity());
    }

    // ====== private methods

    private void createObjectIdentity(ObjectIdentity objectIdentity, PrincipalSid sid) {
        final String sidId = createOrRetrieveSidPrimaryKey(sid, true);
        final String classId = createOrRetrieveClassPrimaryKey(objectIdentity.getType(), true);
        final AclObjectIdentity aclObjId = new AclObjectIdentity();
        aclObjId.setObjectIdClass(classId);
        aclObjId.setObjectIdIdentity(objectIdentity.getIdentifier()
            .toString());
        aclObjId.setOwnerId(sidId);
        aclObjId.setEntriesInheriting(true);
        aclObjIdRepository.save(aclObjId);
    }

    private String createOrRetrieveClassPrimaryKey(String type, boolean allowCreate) {
        AclClass aclClass = aclClassRepository.findOneByClassName(type);
        if (aclClass != null) {
            return aclClass.getId();
        }
        if (allowCreate) {
            aclClass = new AclClass();
            aclClass.setClassName(type);
            aclClass = aclClassRepository.save(aclClass);
            return aclClass.getId();
        }
        return null;
    }

    private String createOrRetrieveSidPrimaryKey(Sid sid, boolean allowCreate) {
        Assert.notNull(sid, "Sid required");
        String sidName;
        boolean sidIsPrincipal = true;
        if (sid instanceof PrincipalSid) {
            sidName = ((PrincipalSid) sid).getPrincipal();
        } else if (sid instanceof GrantedAuthoritySid) {
            sidName = ((GrantedAuthoritySid) sid).getGrantedAuthority();
            sidIsPrincipal = false;
        } else {
            throw new IllegalArgumentException("Unsupported implementation of Sid");
        }

        final List<AclSid> sids = aclSidRepository.findBySidAndPrincipal(sidName, sidIsPrincipal);
        if (!sids.isEmpty()) {
            return sids.get(0)
                .getId();
        }
        if (allowCreate) {
            final AclSid newSid = new AclSid();
            newSid.setPrincipal(sidIsPrincipal);
            newSid.setSid(sidName);
            return aclSidRepository.save(newSid)
                .getId();
        }
        return null;
    }

    private void createEntries(MutableAcl acl) {
        if (acl.getEntries()
            .isEmpty()) {
            return;
        }
        int order = 0;
        for (final AccessControlEntry entry_ : acl.getEntries()) {
            Assert.isTrue(entry_ instanceof AccessControlEntryImpl, "Unknown ACE class");
            final AccessControlEntryImpl entry = (AccessControlEntryImpl) entry_;
            final AclEntry aclEntry = new AclEntry();
            aclEntry.setObjectIdentityId(acl.getId()
                .toString());
            aclEntry.setSid(createOrRetrieveSidPrimaryKey(entry.getSid(), true));
            aclEntry.setOrder(order++);
            aclEntry.setMask(entry.getPermission()
                .getMask());
            aclEntry.setGranting(entry.isGranting());
            aclEntry.setAuditSuccess(entry.isAuditSuccess());
            aclEntry.setAuditFailure(entry.isAuditFailure());
            aclEntryRepository.save(aclEntry);
        }

    }

    private void updateObjectIdentity(MutableAcl acl) {
        String parentId = null;
        if (acl.getParentAcl() != null) {
            Assert.isInstanceOf(ObjectIdentityImpl.class, acl.getParentAcl()
                .getObjectIdentity(), "Implementation only supports ObjectIdentityImpl");
            final ObjectIdentityImpl oii = (ObjectIdentityImpl) acl.getParentAcl()
                .getObjectIdentity();
            parentId = retrieveObjectIdentityPrimaryKey(oii);
        }

        Assert.notNull(acl.getOwner(), "Owner is required in this implementation");

        final String ownerSid = createOrRetrieveSidPrimaryKey(acl.getOwner(), true);
        final AclObjectIdentity aclObjId = aclObjIdRepository.findOne(acl.getId()
            .toString());
        if (aclObjId == null) {
            throw new NotFoundException("Unable to locate ACL to update");
        }
        aclObjId.setParentObjectId(parentId);
        aclObjId.setOwnerId(ownerSid);
        aclObjId.setEntriesInheriting(acl.isEntriesInheriting());
        aclObjIdRepository.save(aclObjId);
    }

    private void clearCacheIncludingChildren(ObjectIdentity objectIdentity) {
        Assert.notNull(objectIdentity, "ObjectIdentity required");
        final List<ObjectIdentity> children = findChildren(objectIdentity);
        if (children != null) {
            for (final ObjectIdentity child : children) {
                clearCacheIncludingChildren(child);
            }
        }
        aclCache.evictFromCache(objectIdentity);
    }
}
