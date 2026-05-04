import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import styles from './AuthPage.module.css'

export default function LoginPage() {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [form, setForm] = useState({ username: '', password: '' })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const submit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)
        try {
            await login(form.username, form.password)
            navigate('/tasks')
        } catch {
            setError('Неверный логин или пароль')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className={styles.page}>
            <div className={styles.glowBg} />
            <div className={styles.card}>
                <div className={styles.logoArea}>
                    <span className={styles.logoMark}>▲</span>
                    <h1 className={styles.title}>AntiProcrastinate</h1>
                    <p className={styles.subtitle}>Войдите в систему</p>
                </div>

                {error && <div className="error-msg">{error}</div>}

                <form onSubmit={submit}>
                    <div className="field">
                        <label className="label">Имя пользователя</label>
                        <input
                            className="input"
                            name="username"
                            placeholder="username"
                            value={form.username}
                            onChange={handle}
                            required
                        />
                    </div>
                    <div className="field">
                        <label className="label">Пароль</label>
                        <input
                            className="input"
                            name="password"
                            type="password"
                            placeholder="••••••••"
                            value={form.password}
                            onChange={handle}
                            required
                        />
                    </div>
                    <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', padding: '12px' }} disabled={loading}>
                        {loading ? 'Вход...' : 'Войти'}
                    </button>
                </form>

                <p className={styles.footer}>
                    Нет аккаунта? <Link to="/register" className={styles.link}>Зарегистрироваться</Link>
                </p>
            </div>
        </div>
    )
}