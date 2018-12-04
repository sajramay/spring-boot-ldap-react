import axios from 'axios'

class AuthService {

    async login(username, password) {
        var fd = new FormData()
        fd.append('j_username', encodeURI(username))
        fd.append('j_password', encodeURI(password))

        try {
            const response = await axios({
                url: '/auth',
                method: 'post',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: fd,
                withCredentials: true
            });

            if (response.status === 200) {
                return { loggedIn: true, error:'', status: response.status }
            } else {
                return { loggedIn: false, error:response.error, status: response.status }
            }
        } catch (err) {
            return { status: err.response.status, error: err.response.statusText }
        }
    }

    async logout() {

        try {
            const response = await axios({
                url: '/logout',
                method: 'post',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                withCredentials: true
            });

            if (response.status === 200) {
                return { loggedIn: true, error:'', status: response.status }
            } else {
                return { loggedIn: false, error:'', status: response.status }
            }
        } catch (err) {
            return { status: err.response.status, error: err.response.statusText }
        }
    }
}

export default new AuthService()