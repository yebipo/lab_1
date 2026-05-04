import { useState } from 'react'
import { NavLink, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import styles from './Sidebar.module.css'

const TASK_SUBS = [
    { status: 'TODO',        label: 'К выполнению', icon: '○' },
    { status: 'IN_PROGRESS', label: 'В процессе',   icon: '◑' },
    { status: 'DONE',        label: 'Выполнено',    icon: '●' },
]

const NAV = [
    { to: '/skills',     icon: '✦', label: 'Навыки' },
    { to: '/categories', icon: '◈', label: 'Категории' },
    { to: '/worklogs',   icon: '◷', label: 'Трекер времени' },
    { to: '/profile',    icon: '◉', label: 'Профиль' },
]

export default function Sidebar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()
    const location = useLocation()
    const [tasksOpen, setTasksOpen] = useState(
        location.pathname.startsWith('/tasks')
    )

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    const isTasksActive = location.pathname.startsWith('/tasks')

    return (
        <aside className={styles.sidebar}>
            <div className={styles.logo}>
                <span className={styles.logoIcon}>▲</span>
                <span className={styles.logoText}>Anti<span>Procrastinate</span></span>
            </div>

            {user && (
                // FIX: клик по юзер-карточке → переход в профиль
                <NavLink
                    to="/profile"
                    className={({ isActive }) =>
                        `${styles.userBadge} ${isActive ? styles.userBadgeActive : ''}`
                    }
                    style={{ textDecoration: 'none', display: 'flex', cursor: 'pointer' }}
                >
                    <div className={styles.avatar}>
                        {user.avatarUrl
                            ? <img src={user.avatarUrl} alt={user.username}
                                   onError={e => { e.target.style.display = 'none' }} />
                            : user.username?.[0]?.toUpperCase()}
                    </div>
                    <div className={styles.userInfo}>
                        <span className={styles.userName}>{user.username}</span>
                        <span className={styles.userLevel}>Уровень {user.level ?? 1}</span>
                    </div>
                </NavLink>
            )}

            <nav className={styles.nav}>
                {/* Задачи с подпунктами */}
                <div>
                    <div className={`${styles.navItem} ${styles.navItemBtn} ${isTasksActive ? styles.active : ''}`}>
                        {/* FIX: клик по тексту/иконке → /tasks (все задачи) */}
                        <NavLink
                            to="/tasks"
                            style={{ display: 'flex', alignItems: 'center', gap: '10px', flex: 1, textDecoration: 'none', color: 'inherit' }}
                            onClick={() => setTasksOpen(true)}
                        >
                            <span className={styles.navIcon}>⚔</span>
                            <span>Задачи</span>
                        </NavLink>
                        {/* Стрелка раскрывает/скрывает подпункты */}
                        <button
                            onClick={() => setTasksOpen(o => !o)}
                            style={{
                                background: 'none', border: 'none', cursor: 'pointer',
                                padding: '0 4px', color: 'inherit', opacity: 0.5,
                                fontSize: '10px',
                                transition: 'transform 0.2s',
                                transform: tasksOpen ? 'rotate(90deg)' : 'rotate(0deg)',
                                display: 'inline-block',
                                lineHeight: 1,
                            }}
                            title={tasksOpen ? 'Свернуть' : 'Развернуть'}
                        >
                            ▸
                        </button>
                    </div>

                    {tasksOpen && (
                        <div className={styles.subNav}>
                            {TASK_SUBS.map(({ status, label, icon }) => (
                                <NavLink
                                    key={status}
                                    to={`/tasks?status=${status}`}
                                    className={() =>
                                        `${styles.subItem} ${location.search === `?status=${status}` ? styles.subActive : ''}`
                                    }
                                >
                                    <span className={styles.subIcon}>{icon}</span>
                                    {label}
                                </NavLink>
                            ))}
                        </div>
                    )}
                </div>

                {NAV.map(({ to, icon, label }) => (
                    <NavLink
                        key={to}
                        to={to}
                        className={({ isActive }) =>
                            `${styles.navItem} ${isActive ? styles.active : ''}`
                        }
                    >
                        <span className={styles.navIcon}>{icon}</span>
                        <span>{label}</span>
                    </NavLink>
                ))}
            </nav>

            <button className={styles.logoutBtn} onClick={handleLogout}>
                <span>⏻</span> Выйти
            </button>
        </aside>
    )
}