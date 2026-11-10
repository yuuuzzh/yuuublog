<template>
    <section class="image-wall">
        <div class="wall-head">
            <span class="wall-kicker">GALLERY · 光影存档</span>
            <h2>把日子，摊开来看</h2>
            <p>悬停任意一张，看它慢慢展开</p>
        </div>
        <div ref="host" class="wall-stage"></div>
    </section>
</template>

<script>
import { mountImageAccordion } from './image-accordion/ImageAccordionEngine.js'

export default {
    name: 'ImageWall',
    data() {
        return {
            controller: null
        }
    },
    mounted() {
        this.controller = mountImageAccordion(this.$refs.host, {
            open: 55,
            reach: 38,
            bounce: 12,
            gap: 10
        })
        var root = this.controller && this.controller.root
        if (!root) return
        var style = document.createElement('style')
        style.textContent = [
            '.motion-body{background:transparent!important}',
            '.acc-scene{background:transparent!important}'
        ].join('')
        root.appendChild(style)
    },
    beforeDestroy() {
        if (this.controller) this.controller.destroy()
    }
}
</script>

<style scoped>
.image-wall {
    margin: 0 0 42px;
    padding: 26px 26px 18px;
    border: 1px solid var(--border);
    border-radius: var(--radius-lg);
    background: var(--surface-2);
    -webkit-backdrop-filter: saturate(160%) blur(14px);
    backdrop-filter: saturate(160%) blur(14px);
    box-shadow: var(--shadow-1);
}

.wall-head {
    margin-bottom: 6px;
}

.wall-kicker {
    font-size: 11px;
    letter-spacing: .18em;
    color: var(--brand);
    text-transform: uppercase;
}

.wall-head h2 {
    margin: 6px 0 4px;
    font-size: 22px;
    letter-spacing: .04em;
    color: var(--text);
}

.wall-head h2::after {
    content: '';
    display: block;
    width: 46px;
    height: 3px;
    margin-top: 9px;
    border-radius: 3px;
    background: linear-gradient(90deg, var(--brand), var(--accent));
}

.wall-head p {
    margin: 0;
    font-size: 13px;
    color: var(--text-weak);
}

.wall-stage {
    width: 100%;
}

@media (max-width: 768px) {
    .image-wall {
        padding: 18px 14px 10px;
        margin-bottom: 26px;
    }

    .wall-head h2 {
        font-size: 18px;
    }
}
</style>
