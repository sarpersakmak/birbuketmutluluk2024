package com.birbuketmutluluk.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SlugGeneratorTest {

    @Test
    void turkceKarakterleriSadelestirir() {
        assertThat(SlugGenerator.toSlug("Soğuk Porselen Gül Buketi"))
                .isEqualTo("soguk-porselen-gul-buketi");
    }

    @Test
    void ozelKarakterleriVeBosluklariTireYapar() {
        assertThat(SlugGenerator.toSlug("Çiçek & Aranjman!")).isEqualTo("cicek-aranjman");
    }

    @Test
    void bastakiSondakiTireleriAtar() {
        assertThat(SlugGenerator.toSlug("  --İstanbul--  ")).isEqualTo("istanbul");
    }

    @Test
    void bosGirdideBosDoner() {
        assertThat(SlugGenerator.toSlug("   ")).isEmpty();
        assertThat(SlugGenerator.toSlug(null)).isEmpty();
    }
}
