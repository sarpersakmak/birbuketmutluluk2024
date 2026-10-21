import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

import ProductCard from "../../components/ProductCard";
import { fetchCategories } from "../../services/categoryService";
import { fetchProducts } from "../../services/productService";

const SORT_OPTIONS = [
  { value: "newest", label: "En Yeni" },
  { value: "priceAsc", label: "Fiyat (Artan)" },
  { value: "priceDesc", label: "Fiyat (Azalan)" },
  { value: "nameAsc", label: "İsme Göre (A-Z)" },
];

export default function ProductListPage() {
  const [searchParams] = useSearchParams();
  const [categories, setCategories] = useState([]);
  const [filters, setFilters] = useState({
    q: "",
    categoryId: searchParams.get("categoryId") || "",
    minPrice: "",
    maxPrice: "",
    sort: "newest",
  });
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Kategorileri bir kez yükle.
  useEffect(() => {
    fetchCategories()
      .then((res) => setCategories(res.data))
      .catch(() => setCategories([]));
  }, []);

  // Filtre/sayfa değişince ürünleri yükle.
  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(false);

    const params = { sort: filters.sort, page, size: 12 };
    if (filters.q.trim()) params.q = filters.q.trim();
    if (filters.categoryId) params.categoryId = filters.categoryId;
    if (filters.minPrice) params.minPrice = filters.minPrice;
    if (filters.maxPrice) params.maxPrice = filters.maxPrice;

    fetchProducts(params)
      .then((res) => {
        if (active) setData(res.data);
      })
      .catch(() => {
        if (active) setError(true);
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [filters, page]);

  const handleFilterChange = (e) => {
    setPage(0);
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  return (
    <section className="bbm-page">
      <h1>Ürünler</h1>

      <div className="bbm-filters">
        <input
          type="search"
          name="q"
          placeholder="Ürün ara..."
          value={filters.q}
          onChange={handleFilterChange}
        />
        <select name="categoryId" value={filters.categoryId} onChange={handleFilterChange}>
          <option value="">Tüm Kategoriler</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
        <input
          type="number"
          name="minPrice"
          placeholder="Min ₺"
          min="0"
          value={filters.minPrice}
          onChange={handleFilterChange}
        />
        <input
          type="number"
          name="maxPrice"
          placeholder="Max ₺"
          min="0"
          value={filters.maxPrice}
          onChange={handleFilterChange}
        />
        <select name="sort" value={filters.sort} onChange={handleFilterChange}>
          {SORT_OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </select>
      </div>

      {loading && <p className="bbm-status">Ürünler yükleniyor...</p>}

      {error && !loading && (
        <p className="bbm-status bbm-error">Ürünler yüklenemedi. Lütfen tekrar deneyin.</p>
      )}

      {!loading && !error && data && data.content.length === 0 && (
        <p className="bbm-status">Aramanıza uygun ürün bulunamadı.</p>
      )}

      {!loading && !error && data && data.content.length > 0 && (
        <>
          <div className="bbm-grid">
            {data.content.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>

          <div className="bbm-pagination">
            <button
              type="button"
              className="bbm-btn bbm-btn-ghost"
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              Önceki
            </button>
            <span>
              Sayfa {data.page + 1} / {Math.max(1, data.totalPages)}
            </span>
            <button
              type="button"
              className="bbm-btn bbm-btn-ghost"
              disabled={data.last}
              onClick={() => setPage((p) => p + 1)}
            >
              Sonraki
            </button>
          </div>
        </>
      )}
    </section>
  );
}
