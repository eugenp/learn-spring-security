## Module 2 - A Full Registration Flow
This is the codebase for Module 2 of [Learn Spring Security](http://bit.ly/github-lss)

### Notes Regarding the SMTP/Email Configuration
Note that even though the email sending logic is configured in the codebase, there is additional SMTP configuration required:

1. Define the `spring.mail.username` and the `spring.mail.password` application properties

2. If you're using Gmail - Google's account security configurations:
    1. **If you don't have 2FA enabled in your account:** You can enable 'less secure apps access' (at least temporarily to test the email functionality) using this link: https://myaccount.google.com/lesssecureapps. Of course, we don't recommend this approach, we suggest configuring 2FA and using the following approach.
    2. **If you have 2FA enabled:** Add an App Password to your account (following these steps: https://support.google.com/accounts/answer/185833) and using that code as the password in the code.
