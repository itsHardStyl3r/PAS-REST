import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    headers: {
        'Content-Type': 'application/json'
    }
});

instance.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

instance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if ((error.response?.status === 401 || error.response?.status === 403) && !originalRequest._retry) {
            originalRequest._retry = true;
            const refreshToken = sessionStorage.getItem('refreshToken');

            if (refreshToken) {
                try {
                    const res = await axios.post('http://localhost:8080/api/v1/auth/refresh', {
                        refreshToken: refreshToken
                    });

                    if (res.status === 200) {
                        const { token, refreshToken: newRefreshToken } = res.data;

                        sessionStorage.setItem('token', token);
                        sessionStorage.setItem('refreshToken', newRefreshToken);

                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        return instance(originalRequest);
                    }
                } catch (refreshError) {
                    console.error("Refresh token wygasł lub jest błędny");
                    sessionStorage.clear();
                    window.location.href = '/';
                    return Promise.reject(refreshError);
                }
            }
        }
        return Promise.reject(error);
    }
);

export default instance;