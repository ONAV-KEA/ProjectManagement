<h1 align="center">
  <br>
  <a href="[projectplanner](https://alphaprojectplanner.azurewebsites.net/)"><img src=https://github.com/ONAV-KEA/ProjectManagement/blob/main/src/main/resources/static/images/projectplanner-logo.png?raw=true" alt="ProjectPlanner" width="500"></a> 
</h1>

<h4 align="center">Alpha Solutions Cost Estimation Tool</h4>

<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=java,js,html,planetscale,spring,bootstrap&theme=light" />
  </a>
</p>

 <h4 align="center">Try the app <a href="https://alphaprojectplanner.azurewebsites.net/">HERE</h4></p> 


## Description
ProjectPlanner is a cost estimation tool that allows for easier project managing. 
    
### Key Features    
* Register project members
* Create, edit, and delete projects
* Assign tasks to specified project member(s)
* Overview of cost estimation
* Create, edit, delete, tasks and subtasks from projects
    
## Installation
To run the project, you first need to install the necessary dependencies and then start the server. You can follow these steps:

1. Clone the repository by running `git clone https://github.com/ONAV-KEA/ProjectManagement.git`
2. Open the `ProjectManagement` folder in your desired IDE and navigate to `src/main/resources/application.properties`
3. Setup a database environment and execute the script in `src/main/resources/sql.db`
4. Execute the following query to insert test data:
    ```
    INSERT INTO user (username, password, first_name, last_name, birthday, role)
    VALUES 
    ('KlausPetersen', 'password', 'Klaus', 'Petersen', '1967-01-01', 'admin'),
    ('J.H.Andreasen', 'password', 'Jakob', 'Huus Andreasen', '1974-09-09', 'admin'),
    ('UlrikThørner', 'password', 'Ulrik', 'Thørner', '1970-04-04', 'admin');
    ```
5. Edit the following fields according to your preferences and save the file. 
    ```
    spring.datasource.url=
    spring.datasource.username=
    spring.datasource.password=
    ```
6. Open your terminal and run `./mvnw spring-boot:run` inside the `ProjectManagement` folder
7. Navigate to `http://localhost:8080` inside your browser
   
## Leader Usage
1. Login to the website
    - You will then be redirected to the admin site, where you have an overview of all users in the system
2. Fill in the user registration form with the required information of your fellow employees
3. Click "Create new profile"
4. Share the information with the appropriate colleague
    
## Contact
If you wish to give us feedback with new ideas, or have a question, feel free to contact one of the following project developers:
- <a href="https://github.com/Teller501">Teller501</a>
- <a href="https://github.com/nicolaiandersson">nicolaiandersson</a>
- <a href="https://github.com/OmarKayed">OmarKayed</a>
- <a href="https://github.com/VictorHanert">VictorHanert</a>
