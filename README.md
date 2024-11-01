# Online-Examination-Module-Backend
A full-stack application to conduct online examinations that include MCQ and programming sections.

Steps to Follow 

1-> Add database connection url in resources/application.properties in spring.datasource.url
2-> Add a admin user using http://localhost:8080/api/auth/register

Note : Creating Admin User through these Api is for now only, afterwards will find a better way of creating Admin User 

Demo Json Structure =>

{
	"email": "admin@gmail.com",
    "password":"admin",
    "role":"ADMIN",
    "fullName":"admin_user"
}



