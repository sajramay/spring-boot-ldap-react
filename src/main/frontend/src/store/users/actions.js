import userService from '../../services/userService';
import * as types from './actionTypes';
import * as authTypes from '../auth/actionTypes';

export function getUsers() {
    return async (dispatch, getState) => {

        dispatch({type: types.LOADING});

        const { users, status } = await userService.getUsers();

        switch (status) {
            case 200:
                dispatch({type: types.LOADED, users });
                break;
            case 401:
                dispatch({type: authTypes.AUTH_ERROR, status });
                break;
            default:
                dispatch({type: authTypes.SERVER_ERROR, status });
        }
    }
}
