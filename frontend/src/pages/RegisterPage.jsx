import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import styles from './AuthPage.module.css'

export default function RegisterPage() {
    const { register } = useAuth()
    const navigate = useNavigate()
    const [form, setForm] = useState({
        username: '', email: '', password: '',
    })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const submit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)
        try {
            // dailyGoalMinutes обязателен в UserCreateDto (@NotNull) — ставим дефолт 60
            await register({ ...form, dailyGoalMinutes: 60 })
            navigate('/tasks')
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка регистрации')
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
                    <p className={styles.subtitle}>Создайте аккаунт</p>
                </div>

                {error && <div className="error-msg">{error}</div>}

                <form onSubmit={submit}>
                    <div className="field">
                        <label className="label">Имя пользователя</label>
                        <input className="input" name="username" placeholder="username" value={form.username} onChange={handle} required />
                    </div>
                    <div className="field">
                        <label className="label">Email</label>
                        <input className="input" name="email" type="email" placeholder="you@example.com" value={form.email} onChange={handle} required />
                    </div>
                    <div className="field">
                        <label className="label">Пароль</label>
                        <input className="input" name="password" type="password" placeholder="Минимум 6 символов" value={form.password} onChange={handle} required />
                    </div>
                    <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', padding: '12px' }} disabled={loading}>
                        {loading ? 'Регистрация...' : 'Зарегистрироваться'}
                    </button>
                </form>

                <p className={styles.footer}>
                    Уже есть аккаунт? <Link to="/login" className={styles.link}>Войти</Link>
                </p>
            </div>
        </div>
    )
}