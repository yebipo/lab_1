import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import styles from './Sidebar.module.css'

const NAV = [
    { to: '/tasks',      icon: '⚔', label: 'Задачи' },
    { to: '/skills',     icon: '✦', label: 'Навыки' },
    { to: '/categories', icon: '◈', label: 'Категории' },
    { to: '/worklogs',   icon: '◷', label: 'Трекер времени' },
    { to: '/profile',    icon: '◉', label: 'Профиль' },
]

export default function Sidebar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    return (
        <aside className={styles.sidebar}>
            <div className={styles.logo}>
                <span className={styles.logoIcon}>▲</span>
                <span className={styles.logoText}>Anti<span>Pro</span></span>
            </div>

            {user && (
                <div className={styles.userBadge}>
                    <div className={styles.avatar}>
                        {user.avatarUrl
                            ? <img src={user.avatarUrl} alt={user.username} />
                            : user.username?.[0]?.toUpperCase()}
                    </div>
                    <div className={styles.userInfo}>
                        <span className={styles.userName}>{user.username}</span>
                        <span className={styles.userLevel}>Уровень {user.level ?? 1}</span>
                    </div>
                </div>
            )}

            <nav className={styles.nav}>
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