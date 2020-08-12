import React from 'react'
import * as authSelectors from '../store/auth/reducer'
import * as authActiokns from '../store/auth/actions'
import { withRouter, Redirect } from "react-router";
import connect from "react-redux/es/connect/connect";
import './css/Login.css'

class Login extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            submitted: false
        };
        this.usernameChange = this.usernameChange.bind(this);
        this.passwordChange = this.passwordChange.bind(this);
        this.handleSubmit   = this.handleSubmit.bind(this);
    }

    usernameChange(event) {
        const { name, value } = event.target;
        this.setState( {username: value});
        this.setState( {submitted: false});
    }

    passwordChange(event) {
        const { name, value } = event.target;
        this.setState( {password: value});
        this.setState( {submitted: false});
    }

    handleSubmit(event) {
        event.preventDefault();
        this.setState( {submitted: true});
        const { username, password } = this.state;
        this.props.dispatch(authActiokns.authenticate(username, password));
    }

    render() {
        if (this.props.loggedIn === true) {
            return <Redirect to="/" push/>
        }

        return (
            <div className="wrapper">
                <form className="form-signin" onSubmit={this.handleSubmit}>
                    <h2 className="form-signin-heading">Please login</h2>
                    <input placeholder="Use 'user1'" type="text" name="username" onChange={this.usernameChange} className="form-control"/>
                    <input placeholder="Use 'password123'" type="password" name="password" onChange={this.passwordChange} className="form-control"/>
                    <hr/>
                    <button className="btn btn-lg btn-primary btn-block" type="submit">Login</button>
                </form>
            </div>
        )
    }
}

function mapStateToProps(state) {
    return authSelectors.getLoginDetails(state)
}

export default withRouter(connect(mapStateToProps)(Login));
