import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import Layout from './components/Layout.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import TasksPage from './pages/TasksPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import CategoriesPage from './pages/CategoriesPage.jsx'
import SkillsPage from './pages/SkillsPage.jsx'
import WorkLogsPage from './pages/WorkLogsPage.jsx'

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route element={<ProtectedRoute />}>
                        <Route element={<Layout />}>
                            <Route path="/" element={<Navigate to="/tasks" replace />} />
                            <Route path="/tasks" element={<TasksPage />} />
                            <Route path="/profile" element={<ProfilePage />} />
                            <Route path="/categories" element={<CategoriesPage />} />
                            <Route path="/skills" element={<SkillsPage />} />
                            <Route path="/worklogs" element={<WorkLogsPage />} />
                        </Route>
                    </Route>
                    <Route path="*" element={<Navigate to="/tasks" replace />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    )
}