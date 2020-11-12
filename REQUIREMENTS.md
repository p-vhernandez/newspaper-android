###### We will describe the functional requirements of the web interface of the newspaper application.

**Usage**
- Application must be able to show the list of articles without login in the app and select the article in order to edit it. 
- **Basic proposal**: select picture from the device in order to add it to the article.
- **Advanced proposal**: add a new Android activity to take the picture.
- REST services are used. (https://sanger.dia.fi.upm.es/pmd-task/). Check doc information about services calls.

**Main screen**
- Show a list of articles available in the server as anonymous user (ordered by date).
- Navigation bar: show all buttons or links for all categories available in the newspaper (national, economy, sports, technology, all).
- Articles shown must include: title, abstract, image thumbnail if exists and category.
- List implemented with ListView or RecyclerView.
- For each row a custom layout will be implemented.
- Every article in the list should be linked to the details page.
- A login button will be included (optional).
- Indicator to show if the user is logged in or not. 
- If user is logged in: articles can be created, edited and removed.
- If user is not logged in: user cannot edit newspaper content.

**Article details**
- Show title, subtitle, abstract, category, body and picture (if it's included).
- If the image is not included, a default image must be shown.
- Modification date and user id who has modified the content must be shown.
- Articles cannot be modified with a form. Just the image can be edited. 

**Login - optional**
- It is not mandatory to have a login. 
  - If it's implemented, user must be logged in to modify the article's picture.
  - It it's not, user can directly access to modify the picture of the selected article.
- New activity or form.
- User will be able to save session using e "remember me" button. API Key will be stored in the app preferences, so no login is needed in next app executions.
- Login button should be changed to Sign out button when user is logged in.


**See notes section for further clarifications**
