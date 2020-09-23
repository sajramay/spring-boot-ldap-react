import React from 'react';
import { NavLink } from 'react-router-dom';
import * as authActions from '../store/auth/actions';
import * as authSelectors from '../store/auth/reducer';
import * as userActions from '../store/users/actions';
import * as userSelectors from '../store/users/reducer';
import { withRouter, Redirect } from "react-router";
import connect from "react-redux/es/connect/connect";
import ReactTable from 'react-table';
import 'react-table/react-table.css';

class Home extends React.Component {

    constructor(props) {
        super(props)
        this.handleLogout = this.handleLogout.bind(this)
        this.props.dispatch(userActions.getUsers())
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
            <h2>Users</h2>
                <div>
                <ReactTable
                    data={this.props.users}
                    columns={
                        [
                            {Header: "ID", accessor: "id", width: 100},
                            {Header: "First Name", accessor: "firstName"},
                            {Header: "Last Name", accessor: "lastName"},
                            {Header: "Total Spent", accessor: "totalSpent"},
                            {Header: "Maximum", accessor: "maximumSpent"}
                        ]
                    }
                    defaultPageSize={10}
                    className="-striped -highlight"
                >
                </ReactTable>
                </div>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {
        loggedIn : authSelectors.getLoginDetails(state).loggedIn,
        users : userSelectors.getUsers(state).users
    }
}

export default withRouter(connect(mapStateToProps)(Home));
