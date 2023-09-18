# compsci715-group4 iBird: a game that combines of exergame and learning birds
 
 
## Features
todo
Web Crafter is a low-code website building platform aimed to help people who don't know website programing to buildup their own website by drag and drop.
- Login with Google authentication
- Provide basic widgets for user to buildup the website
- Provide flexible drag-and-drop feature for people to add and modify the widget for their webpage in canvas
- Styles and properfies of widget can be modified easily from setting panel
- Modifiable and customizable switch/button/chip for multiple usage.
- support event for some of the widget (e.g: support onclick event on button)
- Auto loading for the canvas if user already saved one


## Tech stacks
todo

The main Tech stacks we are using in this project are listed below:
  - craft.js: An opensource framework for building web builder application
  - material UI: Styling library used for both styling the application and widget in the application
  - vite: light-weighted frontend react CLI
  - node.js: open-source, cross-platform JavaScript runtime environment
  - express: minimal and flexible Node.js web application framework that provides a robust set of features for web and mobile applications
  - Jest: testing framework designed to ensure correctness of any JavaScript codebase
  - Swagger: Dashbord for managing / testing backend APIs


## Running Project
todo

1. Setup nodejs environment, make sure MongoDB is installed on the computer.

2. Run the project

```bash
npm run start-all
```

This command will start up frontend and backend at the same time so you don't need to cd to different folder to start them seperately.

Or if you are running the project first time:

```bash
npm run start-all-first-time
```
This command will install all needed package for frontend and backend as well as starting up them at the same time

3. If the command above doesn't work, then you needed to install the packages and start up frontend and backend manually

Run the command below to start frontend
```bash
npm install #(only if you are running the project at the first time)
npm run start
```

then cd to `server` folder, run the command below to start backend
```bash
npm install #(only if you are running the project at the first time)
node server.js
```


## ScreenShots
todo

## Contributors

- Zixuan Wen (zwen655)
- Brenda San Germán (bsan361)
- Ziqi Zhong (zzho500)
- Ariel Zhuang (rzhu035)
  
### guidelines for implementation
---
1. ui is located in `app\src\main\res`, feel free to modify the corresponding part for the project (android studio provides a preview for each ui so it's pretty easy to see the actual layout)
2. logic/event related to ui components are located in `app\src\main\java\..\..\..`, need to add event corresponding to the newly added ui component here.
3. guides for programming an android app: https://developer.android.com/develop/ui/views/components/button#java
4. NN api for android app: https://developer.android.com/ndk/guides/neuralnetworks
