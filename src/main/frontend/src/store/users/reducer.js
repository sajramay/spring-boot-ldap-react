
import * as actionTypes from './actionTypes';

const initialState = {
    users: []
};

export default function reducer(state=initialState, action={}) {
    switch (action.type) {
        case actionTypes.LOADING:
            return { users: [] }
        case actionTypes.LOADED:
            return { users: action.users }
        default:
            return state;
    }
}

export function getUsers(state) {
    return { users : state.users.users }
}
