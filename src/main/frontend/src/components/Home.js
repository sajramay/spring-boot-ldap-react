import React from 'react';
import { NavLink } from 'react-router-dom'
import * as authActions from '../store/auth/actions'
import * as authSelectors from '../store/auth/reducer'
import { withRouter, Redirect } from "react-router";
import connect from "react-redux/es/connect/connect";

class Home extends React.Component {

    constructor(props) {
        super(props)
        this.handleLogout = this.handleLogout.bind(this)
    }

    handleLogout() {
        this.props.dispatch(authActions.logout())
    }

    renderNavBar() {
        return (
        <nav className="navbar navbar-expand-sm bg-dark navbar-dark sticky-top">
            <ul className="navbar-nav">
                <li className="nav-item">
                    <NavLink className="nav-link" to="/">Home</NavLink>
                </li>
            </ul>
            <ul className="navbar-nav ml-auto">
                <li className="nav-item">
                    <button className="btn btn-sm btn-danger" onClick={this.handleLogout}>Logout</button>
                </li>
            </ul>
        </nav>)
    }

    render() {
       if (this.props.loggedIn === false) {
           return <Redirect to="/login" push/>
       }

        return (
            <div>
            {this.renderNavBar()}
            <h2>Home page</h2>
                Render data from the server here
            </div>
        );
    }
}

function mapStateToProps(state) {
    return { loggedIn : authSelectors.getLoginDetails(state).loggedIn}
}

export default withRouter(connect(mapStateToProps)(Home));
