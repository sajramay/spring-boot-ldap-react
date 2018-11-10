import React, { Component } from 'react';
import './App.css';
import { Route } from 'react-router-dom'
import Home from './components/Home'
import Other from './components/Other'
import Login from './components/Login'

class App extends Component {
  render() {
    return (
      <div className="App">
          <Route exact path="/" component={Home}></Route>
          <Route exact path="/login" component={Login}></Route>
          <Route exact path="/other" component={Other}></Route>
      </div>
    );
  }
}

export default App;
