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
    const [avatarError, setAvatarError] = useState(false)

    const startEdit = () => {
        setForm({
            username: user.username,
            email: user.email,
            oldPassword: '',
            newPassword: '',
            dailyGoalMinutes: user.dailyGoalMinutes,
            avatarUrl: user.avatarUrl || '',
        })
        setEditing(true)
        setError('')
        setSuccess(false)
    }

    const handle = (e) => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))

    const submit = async (e) => {
        e.preventDefault()

        // Если заполнено поле нового пароля, проверяем условия
        if (form.newPassword && form.newPassword.trim() !== '') {
            if (!form.oldPassword || form.oldPassword.trim() === '') {
                setError('Введите старый пароль для смены на новый')
                return
            }
            if (form.newPassword.length < 6) {
                setError('Новый пароль должен быть не менее 6 символов')
                return
            }
        }

        setSaving(true)
        setError('')
        try {
            const dto = {
                ...form,
                dailyGoalMinutes: Number(form.dailyGoalMinutes),
                // Отправляем null, если пароли не заполнены
                oldPassword: form.oldPassword || null,
                newPassword: form.newPassword || null
            }

            await updateMe(dto)
            await refreshUser()
            setEditing(false)
            setAvatarError(false)
            setSuccess(true)
            setTimeout(() => setSuccess(false), 3000)
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка обновления')
        } finally {
            setSaving(false)
        }
    }

    if (!user) return <div className="spinner" />

    const showAvatar = user.avatarUrl && !avatarError

    return (
        <div className={styles.page}>
            <div className="page-header">
                <h1 className="page-title">Профиль <span>/ Profile</span></h1>
            </div>

            <div className={styles.grid}>
                <div className={`card ${styles.profileCard}`}>
                    <div className={styles.avatarWrap}>
                        <div className={styles.avatar}>
                            {showAvatar
                                ? (
                                    <img
                                        src={user.avatarUrl}
                                        alt={user.username}
                                        onError={() => setAvatarError(true)}
                                        style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 'inherit' }}
                                    />
                                )
                                : user.username?.[0]?.toUpperCase()
                            }
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
                    </div>

                    {!editing && (
                        <button
                            className="btn btn-ghost"
                            style={{ width: '100%', justifyContent: 'center', marginTop: '8px' }}
                            onClick={startEdit}
                        >
                            ✎ Редактировать профиль
                        </button>
                    )}
                </div>

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
                                    <label className="label">Дневная цель (минут)</label>
                                    <input className="input" name="dailyGoalMinutes" type="number" min="1" value={form.dailyGoalMinutes} onChange={handle} required />
                                </div>
                                <div className="field">
                                    <label className="label">URL аватара</label>
                                    <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                                        <input
                                            className="input"
                                            name="avatarUrl"
                                            placeholder="https://example.com/avatar.jpg"
                                            value={form.avatarUrl}
                                            onChange={handle}
                                            style={{ flex: 1 }}
                                        />
                                        {form.avatarUrl && (
                                            <img
                                                src={form.avatarUrl}
                                                alt="preview"
                                                style={{ width: 36, height: 36, objectFit: 'cover', borderRadius: '50%', border: '1px solid var(--border)', flexShrink: 0 }}
                                                onError={e => { e.target.style.display = 'none' }}
                                                onLoad={e => { e.target.style.display = 'block' }}
                                            />
                                        )}
                                    </div>
                                </div>

                                {/* Поля паролей в конце списка */}
                                <div className="field">
                                    <label className="label">Старый пароль</label>
                                    <input
                                        className="input"
                                        name="oldPassword"
                                        type="password"
                                        placeholder="Введите старый пароль"
                                        value={form.oldPassword}
                                        onChange={handle}
                                    />
                                </div>
                                <div className="field">
                                    <label className="label">Новый пароль</label>
                                    <input
                                        className="input"
                                        name="newPassword"
                                        type="password"
                                        placeholder="Минимум 6 символов"
                                        value={form.newPassword}
                                        onChange={handle}
                                    />
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
                                    ['Имя пользователя', user.username],
                                    ['Email', user.email],
                                    ['Уровень', user.level ?? 1],
                                    ['Дневная цель', `${user.dailyGoalMinutes} минут`],
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