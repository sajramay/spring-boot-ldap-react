
import * as types from './actionTypes'

const initialState = {
    loggedIn: false,
    username: undefined,
    error: ''
};

export default function reduce(state=initialState, action={}) {
    switch (action.type) {
        case types.LOGGED_IN:
            return {loggedIn: true, username : action.username, error:''};
        case types.LOGGING_IN:
        case types.LOGGED_OUT:
            return {loggedIn: false, username : undefined, error:''};
        case types.AUTH_ERROR:
            return {loggedIn: false, username : undefined, error:'Authentication Error'};
        case types.SERVER_ERROR:
            return {loggedIn: false, username : undefined, error:'Server Error'};
        default:
            return state
    }
}

export function getLoginDetails(state) {
    return {loggedIn: state.auth.loggedIn, username: state.auth.username, error: state.auth.error }
}
