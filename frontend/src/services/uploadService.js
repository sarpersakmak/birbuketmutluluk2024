import api from "./api";

/**
 * Görseli multipart olarak yükler. Content-Type'ı axios'un FormData sınırı (boundary)
 * ile otomatik ayarlaması için header'ı burada bilerek belirtmiyoruz (undefined).
 */
export const uploadImage = (file) => {
  const form = new FormData();
  form.append("file", file);
  return api.post("/admin/uploads", form, {
    headers: { "Content-Type": undefined },
  });
};
