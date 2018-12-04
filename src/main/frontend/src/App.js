import React, { Component } from 'react';
import './App.css';
import { Route } from 'react-router-dom'
import Home from './components/Home'
import Login from './components/Login'
import * as authSelectors from './store/auth/reducer'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'

class App extends Component {
  render() {
    return (
      <div className="App">
          <Route exact path="/" component={Home}></Route>
          <Route exact path="/login" component={Login}></Route>
      </div>
    );
  }
}

function mapStateToProps(state) {
    return { loggedIn : authSelectors.getLoginDetails(state).loggedIn}
}

export default withRouter(connect(mapStateToProps)(App));
