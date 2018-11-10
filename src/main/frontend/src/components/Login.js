import React from 'react'
import './css/Login.css'

class Login extends React.Component {

    onChange() {

    }

    render() {
        return (
            <div className="wrapper">
                <form className="form-signin">
                    <h2 className="form-signin-heading">Please login</h2>
                    <input placeholder="username" type="text" name="username" onChange={this.onChange} className="form-control"/>
                    <input placeholder="password" type="text" name="password" onChange={this.onChange} className="form-control"/>
                    <hr/>
                    <button className="btn btn-lg btn-primary btn-block" type="submit">Login</button>
                </form>
            </div>
        )
    }
}

export default Login;
