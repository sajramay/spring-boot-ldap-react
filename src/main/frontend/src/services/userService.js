import axios from 'axios'
import * as _ from 'lodash';

class UserService {

    async getUsers() {
        try {
            const response = await axios({
                url: '/graphql',
                method: 'post',
                data: {
                    query: `
                        query {
                          allUsers {
                            id
                            firstName
                            lastName
                            totalSpent
                            maximumSpent
                          }
                        }                        
                    `
                },
                withCredentials: true
            });

            console.log(response.data.data.users)

            if (response.status === 200) {
                return { users: response.data.data.allUsers, status: response.status }
            } else {
                return { loggedIn: false, error:response.error, status: response.status }
            }
        } catch (err) {
            return { status: err.response.status, error: err.response.statusText }
        }
    }
}

export default new UserService()
