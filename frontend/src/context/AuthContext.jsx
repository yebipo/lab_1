import { createContext, useContext, useState, useEffect } from 'react'
import { login as apiLogin, register as apiRegister } from '../api/authApi.js'
import { getMe } from '../api/userApi.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const token = localStorage.getItem('token')
        if (token) {
            getMe()
                .then((res) => setUser(res.data))
                .catch(() => localStorage.removeItem('token'))
                .finally(() => setLoading(false))
        } else {
            setLoading(false)
        }
    }, [])

    const login = async (username, password) => {
        const { data } = await apiLogin(username, password)
        localStorage.setItem('token', data.token)
        const me = await getMe()
        setUser(me.data)
        return me.data
    }

    const register = async (dto) => {
        await apiRegister(dto)
        return login(dto.username, dto.password)
    }

    const logout = () => {
        localStorage.removeItem('token')
        setUser(null)
    }

    const refreshUser = async () => {
        const me = await getMe()
        setUser(me.data)
        return me.data
    }

    return (
        <AuthContext.Provider value={{ user, loading, login, register, logout, refreshUser }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)