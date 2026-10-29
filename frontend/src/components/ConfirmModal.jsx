import { createPortal } from "react-dom";

// Basit onay modalı (silme işlemleri için - bölüm 40). Body'ye portal edilir.
export default function ConfirmModal({
  open,
  title = "Emin misiniz?",
  message,
  confirmText = "Evet, sil",
  cancelText = "Vazgeç",
  onConfirm,
  onCancel,
  busy = false,
}) {
  if (!open) return null;

  return createPortal(
    <div className="bbm-modal-overlay" onClick={onCancel}>
      <div className="bbm-modal" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        {message && <p className="bbm-muted">{message}</p>}
        <div className="bbm-modal-actions">
          <button type="button" className="bbm-btn bbm-btn-ghost" onClick={onCancel} disabled={busy}>
            {cancelText}
          </button>
          <button type="button" className="bbm-btn bbm-btn-danger" onClick={onConfirm} disabled={busy}>
            {busy ? "İşleniyor..." : confirmText}
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
}
