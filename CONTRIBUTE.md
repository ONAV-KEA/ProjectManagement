# Thank you for contributing to ProjectPlanner
We appriciate any feedback that is thrown at us to create the perfect website for your project planning! However, we do have some guidelines for contributing, so that we all agree on how to provide to this project.

# How to contribute

## Pull Requests
1. Fork the repository.
2. Clone your fork to your local machine.
3. Create a new branch for your changes.
4. Make your changes in your new branch.
5. Test your changes to make sure they work as expected.
6. Push your changes to your forked repository.
7. Create a pull request from your forked repository to our repository.

When creating a pull request, please include a description of the changes you made and why you made them. If applicable, please include screenshots or code snippets to help us understand the changes.

## Setup PlanetScale database
We are using PlanetScale service to setup our database, but you can chose whatever you like, as long as you use the script below.

Here are a few steps to setup a PlanetScale database:

1. Create a user on https://planetscale.com/ *(You can use your GitHub user to register.)*
2. Once signed in, click **"New Database"** and give it a name.
3. Go to the **"Console"** tab and paste in the script from our resources:
https://github.com/ONAV-KEA/ProjectManagement/blob/main/src/main/resources/sql.db

4. Once setup, you should set your environment variables in your desired IDE, or just modify the fields in ```application.properties```
```
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```
5. Replace the values inside **${}** with your information, you can get by tapping **"Connect"** on PlanetScale and selecting **"Java"**
6. This will give you your URL, Username and Password!

# Guidelines
- We adhere to the standard Java code guidelines, and we advise you do the same
- Make comments for what your methods do
- Ensure that your code works

# Code Reviews
All pull requests will be reviewed by the project maintainers. Please be patient while waiting for your pull request to be reviewed.

During the review process, the maintainers may request changes to your code. Please be open to feedback and willing to make changes to your code if necessary.

Once your pull request is approved, it will be merged into the main branch.

## Feedback
If you wish to give us feedback with new ideas, you can contact one of the following members:
- <a href="https://github.com/Teller501">Anders</a>
- <a href="https://github.com/nicolaiandersson">Nicolai</a>
- <a href="https://github.com/OmarKayed">Omar</a>
- <a href="https://github.com/VictorHanert">Victor</a>
