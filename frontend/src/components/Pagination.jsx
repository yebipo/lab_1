import styles from './Pagination.module.css'

export default function Pagination({ page, totalPages, onChange }) {
    if (totalPages <= 1) return null
    return (
        <div className={styles.pagination}>
            <button
                className={styles.btn}
                disabled={page === 0}
                onClick={() => onChange(page - 1)}
            >← Назад</button>
            <span className={styles.info}>{page + 1} / {totalPages}</span>
            <button
                className={styles.btn}
                disabled={page >= totalPages - 1}
                onClick={() => onChange(page + 1)}
            >Вперёд →</button>
        </div>
    )
}