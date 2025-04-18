# Learn Spring Security Core - A Full Registration Flow
This is the codebase for Module 'A Full Registration Flow' of [Learn Spring Security Core](https://www.baeldung.com/course-lssc-r7ujk)

### Notes Regarding the SMTP/Email Configuration
Note that even though the email sending logic is configured in the codebase, there is additional SMTP configuration required:

1. Define the `spring.mail.username` and the `spring.mail.password` application properties

2. If you're using Gmail - Google's account security configurations:
    1. Enable 2FA (following these steps: https://support.google.com/accounts/answer/185839).
    2. Add an App Password to your account (following these steps: https://support.google.com/accounts/answer/185833) and using that code as the password in the code.