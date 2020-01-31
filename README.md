# react-native-bring-to-foreground

React Native library to bring the application running in the background to foreground.
If the screen is locked, the lock screen shows up first.

## Getting started

`$ npm install react-native-bring-to-foreground --save` or
`$ yarn add react-native-bring-to-foreground`

## Usage
```javascript
import BringToForeground from 'react-native-bring-to-foreground';

// Launch the app and pass the given app launch parameters
// Can be called from a headless task
BringToForeground.open({a: 1});

// Read app launch parameters
const extraData = await BringToForeground.getLaunchParameters();

// Clear app launch parameters
BringToForeground.clearLaunchParameters();
```
