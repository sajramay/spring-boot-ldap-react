import authService from '../../services/authServices'
import * as types from './actionTypes'

export function authenticate(username, password) {
    return async(dispatch, getState) => {
        dispatch({type: types.LOGGING_IN});

        const { status, error } = await authService.login(username, password)

        switch (status) {
            case 200:
                dispatch({type: types.LOGGED_IN, username });
                break;
            case 401:
                dispatch({type: types.AUTH_ERROR, status, error });
                break;
            default:
                dispatch({type: types.SERVER_ERROR, status, error });
        }
    }
}

export function logout() {
    return async (dispatch, getState) => {
        const { status, error } = await authService.logout();

        switch (status) {
            case 200:
                dispatch({type: types.LOGGED_OUT, username:undefined});
                break;
            case 401:
                dispatch({type: types.AUTH_ERROR, status, error });
                break;
            default:
                dispatch({type: types.SERVER_ERROR, status, error });
        }
    }
}