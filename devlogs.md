# Dev logs for this project

---
Dec 18, 2025

Im wrting this logs for my future self.

I made this project for handling file uploads and i want this api to be flexible for any projects or applications that has file uploads. cater the file uploads and file saving in db without repeatedly implementing this feature over and over again in every project.

used <https://start.spring.io/> to generate the pom.xml and dependencies.

for db and data persistency I already have installed postgresql i tried to use h2 db, but it has some problems hence, im just going to move back again to postgre so i must use postgre while developing.

also still learning postgre and test cases, Im using SpringbootTest with Junit dependency.

Now, I am done developing the FileManagmentService.java it has a UploadFile() which return a Metadata

so next ill be making a controller for it so that it can be used while running
