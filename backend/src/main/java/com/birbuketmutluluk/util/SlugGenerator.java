package com.birbuketmutluluk.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Türkçe karakterleri sadeleştirip SEO dostu slug üretir (bölüm 30).
 * Örn: "Soğuk Porselen Gül Buketi" -> "soguk-porselen-gul-buketi"
 */
public final class SlugGenerator {

    private static final Locale TR = Locale.forLanguageTag("tr-TR");

    private SlugGenerator() {
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String s = input.trim().toLowerCase(TR);

        // Türkçe'ye özgü harfleri ASCII karşılıklarına çevir.
        s = s.replace('ı', 'i')
                .replace('ğ', 'g')
                .replace('ü', 'u')
                .replace('ş', 's')
                .replace('ö', 'o')
                .replace('ç', 'c');

        // Kalan aksanları temizle (ör. é -> e).
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Harf/rakam dışını tireye çevir, baştaki/sondaki tireleri at.
        s = s.replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");

        return s;
    }
}
