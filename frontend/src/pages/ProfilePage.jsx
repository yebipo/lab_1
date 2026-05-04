import { useState } from 'react'
import { useAuth } from '../context/AuthContext.jsx'
import { updateMe } from '../api/userApi.js'
import styles from './ProfilePage.module.css'

export default function ProfilePage() {
    const { user, refreshUser } = useAuth()
    const [editing, setEditing] = useState(false)
    const [form, setForm] = useState(null)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState(false)

    const startEdit = () => {
        setForm({
            username: user.username,
            email: user.email,
            password: '',
            dailyGoalMinutes: user.dailyGoalMinutes,
            avatarUrl: user.avatarUrl || '',
        })
        setEditing(true)
        setError('')
        setSuccess(false)
    }

    const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const submit = async (e) => {
        e.preventDefault()
        setSaving(true)
        setError('')
        try {
            const dto = { ...form, dailyGoalMinutes: Number(form.dailyGoalMinutes) }
            if (!dto.password) delete dto.password
            await updateMe(dto)
            await refreshUser()
            setEditing(false)
            setSuccess(true)
            setTimeout(() => setSuccess(false), 3000)
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка обновления')
        } finally {
            setSaving(false)
        }
    }

    if (!user) return <div className="spinner" />

    return (
        <div className={styles.page}>
            <div className="page-header">
                <h1 className="page-title">Профиль <span>/ Profile</span></h1>
            </div>

            <div className={styles.grid}>
                {/* Profile card */}
                <div className={`card ${styles.profileCard}`}>
                    <div className={styles.avatarWrap}>
                        <div className={styles.avatar}>
                            {user.avatarUrl
                                ? <img src={user.avatarUrl} alt={user.username} />
                                : user.username?.[0]?.toUpperCase()}
                        </div>
                        <div className={styles.levelBadge}>Ур. {user.level ?? 1}</div>
                    </div>
                    <h2 className={styles.name}>{user.username}</h2>
                    <p className={styles.email}>{user.email}</p>

                    <div className={styles.stats}>
                        <div className={styles.statItem}>
                            <span className={styles.statValue}>{user.level ?? 1}</span>
                            <span className={styles.statLabel}>Уровень</span>
                        </div>
                        <div className={styles.statItem}>
                            <span className={styles.statValue}>{user.dailyGoalMinutes}</span>
                            <span className={styles.statLabel}>Мин/день</span>
                        </div>
                        <div className={styles.statItem}>
                            <span className={styles.statValue}>{user.id}</span>
                            <span className={styles.statLabel}>ID</span>
                        </div>
                    </div>

                    {!editing && (
                        <button className="btn btn-ghost" style={{ width: '100%', justifyContent: 'center', marginTop: '8px' }} onClick={startEdit}>
                            ✎ Редактировать профиль
                        </button>
                    )}
                </div>

                {/* Edit form or info */}
                <div className="card">
                    {success && (
                        <div style={{ background: 'var(--green-dim)', border: '1px solid var(--green)', color: 'var(--green)', borderRadius: 'var(--radius)', padding: '10px 14px', marginBottom: '16px', fontSize: '13px' }}>
                            ✓ Профиль обновлён
                        </div>
                    )}

                    {editing ? (
                        <>
                            <h3 className={styles.sectionTitle}>Редактировать</h3>
                            {error && <div className="error-msg">{error}</div>}
                            <form onSubmit={submit}>
                                <div className="field">
                                    <label className="label">Имя пользователя</label>
                                    <input className="input" name="username" value={form.username} onChange={handle} required />
                                </div>
                                <div className="field">
                                    <label className="label">Email</label>
                                    <input className="input" name="email" type="email" value={form.email} onChange={handle} required />
                                </div>
                                <div className="field">
                                    <label className="label">Новый пароль (оставьте пустым для сохранения текущего)</label>
                                    <input className="input" name="password" type="password" placeholder="••••••••" value={form.password} onChange={handle} />
                                </div>
                                <div className="field">
                                    <label className="label">Дневная цель (минут)</label>
                                    <input className="input" name="dailyGoalMinutes" type="number" min="1" value={form.dailyGoalMinutes} onChange={handle} required />
                                </div>
                                <div className="field">
                                    <label className="label">URL аватара</label>
                                    <input className="input" name="avatarUrl" placeholder="https://..." value={form.avatarUrl} onChange={handle} />
                                </div>
                                <div style={{ display: 'flex', gap: '10px' }}>
                                    <button type="button" className="btn btn-ghost" onClick={() => setEditing(false)}>Отмена</button>
                                    <button type="submit" className="btn btn-primary" disabled={saving}>
                                        {saving ? 'Сохранение...' : 'Сохранить'}
                                    </button>
                                </div>
                            </form>
                        </>
                    ) : (
                        <>
                            <h3 className={styles.sectionTitle}>Информация</h3>
                            <div className={styles.infoTable}>
                                {[
                                    ['ID пользователя', user.id],
                                    ['Имя пользователя', user.username],
                                    ['Email', user.email],
                                    ['Уровень', user.level ?? 1],
                                    ['Дневная цель', `${user.dailyGoalMinutes} минут`],
                                    ['URL аватара', user.avatarUrl || '—'],
                                ].map(([k, v]) => (
                                    <div key={k} className={styles.infoRow}>
                                        <span className={styles.infoKey}>{k}</span>
                                        <span className={styles.infoVal}>{v}</span>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    )
}