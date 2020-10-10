# 159_356_Capstone_Prototype

## API documentation
Invoke URL (dev): https://mkwhjgbfl6.execute-api.ap-southeast-2.amazonaws.com/dev

*Methods marked with an asterisk are not implemented*
METHOD | PATH | REQUEST FORMAT | DESCRIPTION
--- | --- | :--- | ---
Add questions | /questions/add (POST) | `{ "questions": [ {"context":"Test context"...} ... ] }` | Used to add questions to the database. Question state is automatically designated based on the user's role<br>Required parameters are: context, details, reason, questionType, choices, answer, tags<br>Optional params are: image
Get question from ID | /questions/get (POST) | `{ "ID":"XXXXXXXXX" }` | Gets a single question based on the ID given
Get questions from tags | /questions/get (POST) | `{ "tags": ["tag1"...] }` | Gets a collection of questions which are tagged with the tags requested. Tags are EXCLUSIVE; only questions with all requested tags will be returned (Can be combined with state)
Get questions from state | /questions/get (POST) | `{ "state": ["TEST"...] }` | Gets questions based on state (TEST, PENDING, ACTIVE or ARCHIVED). States are INCLUSIVE; questions with at least one of the requested tags will be returned (Can be combined with tags)
Get question statistics* | /questions/summary?question=XXXXXXXXX (GET) | *N/A* | Gets summary statistics for a single question
Delete question | /questions/delete (POST) | `{ "ID":"XXXXXXXXXX" }` | Deletes a single question based on the ID given
Edit question | /questions/edit (POST) | `{ "ID":"XXXXXXXXXX", "changes": { "context":"Test context"... } }` | Changes the attributes of a single question
Add users* | /users/add (POST) | `{ "users": [ {"username":"testUser123"...} ... ] }` | Used to add users to the database, as well as Cognito.<br>Required parameters are: username, password, email, name
Get users* | /users/get (GET) | *N/A* | Gets a list of all users
Get user info* | /users/info?user=XXXX (GET) | *N/A* | Gets summary information for a user, including exam performance statistics
Delete user* | /users/delete (POST) | `{ "user":"XXXX" }` | Deletes a user from the database and Cognito
Edit user* | /users/edit (POST) | `{ "user":"XXXX", "changes": { "name":"John Smith"... } }` | Changes a user's attributes<br>Unchangeable attributes: username, email, password
Get full summary* | /summary (GET) | *N/A* | Gets the summary for examiner dashboard
Start a mock exam* | /exam/start (POST) | `{ "type":"MOCK" }` | Starts an informal mock exam. Questions are automatically selected.
Start practice questions | /exam/start (POST) | `{ "type":"PRACTICE", "questions":{"*tagName*":##...} }` | Starts a set of practice questions. By providing pairs of tagNames to the number of questions of that tag, you can specialise what kinds of questions are to be included.
Get question from exam* | /exam/get?question=## (GET) | *N/A* | Gets a question's details from the current in-progress exam. Fails if there is no exam in progress
Get summary of current exam* | /exam/summary (GET) | *N/A* | Gets a summary of the current exam in progress
Submit exam* | /exam/submit (POST) | *N/A* | Submits current exam results to the database and ends the exam
### Practice questions requests
- Providing a tagName-number pair with an empty tagName will grab questions irrespective of tags.
- If there are not enough questions to fulfil a tagName-number pair, the resulting exam will contain as many questions that fit the pair as possible.
- The resulting exam is guaranteed to contain no duplicate questions.
