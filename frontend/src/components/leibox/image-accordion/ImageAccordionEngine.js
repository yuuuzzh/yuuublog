// LeiBox image-accordion 引擎（Vue2/Babel6 移植：去可选链、演示图替换为本站静态图、容器改全宽）
// CSS 内的设计参考署名保留：Bencho by Lorenzo Cabra (MIT).
// src/lib/motion-core/image-accordion.mjs
import { createSurface } from "../motion-runtime";
var WIDTH = 1080;
var HEIGHT = 320;
var MAX_RADIUS = 12;
var clamp = (value, min, max) => Math.min(max, Math.max(min, value));
var setting = (value, fallback, max = 100) => clamp(Number.isFinite(value) ? value : fallback, 0, max);
function createImageAccordion(host, initial, css, defaults) {
  const scope = createSurface(host, {
    width: WIDTH,
    height: HEIGHT,
    responsive: true,
    css,
    markup: '<div class="acc-scene"><div class="acc" role="group" aria-label="Image accordion"></div></div>'
  }, initial);
  const { root, on, reduced, requestAnimationFrame, cancelAnimationFrame } = scope;
  const strip = root.querySelector(".acc");
  const doc = host.ownerDocument;
  const browser = doc.defaultView;
  let options = { open: 50, reach: 35, bounce: 10, gap: 8, ...initial };
  let cells = [];
  let hovered = -1;
  let focused = -1;
  let pinned = -1;
  let frame = 0;
  let previousTime = 0;
  let destroyed = false;
  function paint() {
    const lift = 0.4 + setting(options.open, 50) * 0.028;
    for (const cell of cells) {
      cell.button.style.flexGrow = Math.max(0.05, 1 + lift * cell.value / 100).toFixed(4);
    }
  }
  function settle() {
    if (frame) cancelAnimationFrame(frame);
    frame = 0;
    for (const cell of cells) {
      cell.value = cell.target;
      cell.velocity = 0;
    }
    paint();
  }
  function step(time) {
    const dt = previousTime ? clamp((time - previousTime) / 16.67, 0, 2.5) : 1;
    previousTime = time;
    const tune = setting(options.bounce, 10) / 100;
    const stiffness = 0.08 + tune * 0.16;
    const decay = Math.pow(0.62 + tune * 0.2, dt);
    let moving = false;
    for (const cell of cells) {
      cell.velocity = (cell.velocity + (cell.target - cell.value) * stiffness * dt) * decay;
      cell.value += cell.velocity * dt;
      if (Math.abs(cell.target - cell.value) < 0.02 && Math.abs(cell.velocity) < 0.02) {
        cell.value = cell.target;
        cell.velocity = 0;
      } else {
        moving = true;
      }
    }
    paint();
    frame = moving ? requestAnimationFrame(step) : 0;
  }
  function sync() {
    const active = hovered >= 0 ? hovered : focused >= 0 ? focused : pinned;
    const reach = 0.35 + setting(options.reach, 35) * 9e-3;
    cells.forEach((cell, index) => {
      cell.target = active < 0 ? 0 : 100 * Math.exp(-((Math.abs(index - active) / reach) ** 2));
      cell.button.toggleAttribute("data-open", index === active);
      cell.button.setAttribute("aria-pressed", String(index === pinned));
    });
    if (reduced() || doc.hidden) {
      settle();
      return;
    }
    if (!frame && cells.some((cell) => cell.value !== cell.target || cell.velocity !== 0)) {
      previousTime = 0;
      frame = requestAnimationFrame(step);
    }
  }
  function applyLayout() {
    const gap = setting(options.gap, 8, 20);
    strip.style.gap = `${gap}px`;
    strip.style.setProperty("--acc-radius", `${Math.min(MAX_RADIUS, gap)}px`);
    paint();
  }
  function rebuild() {
    hovered = focused = pinned = -1;
    const images = options.images;
    const items = images && images.length > 0 && images.length <= 5 ? images : defaults;
    cells = items.map(({ name, src }) => {
      const button = doc.createElement("button");
      button.type = "button";
      button.className = "acc-cell";
      button.setAttribute("aria-label", name);
      const image = doc.createElement("img");
      image.alt = "";
      image.draggable = false;
      image.src = src;
      button.append(image);
      return { button, value: 0, target: 0, velocity: 0 };
    });
    strip.replaceChildren(...cells.map((cell) => cell.button));
    applyLayout();
    sync();
  }
  const indexOf = (node) => cells.findIndex((cell) => cell.button === node);
  on(strip, "pointerover", (event) => {
    if (event.pointerType === "touch") return;
    const index = indexOf(event.target);
    if (index < 0 || index === hovered) return;
    hovered = index;
    sync();
  });
  on(strip, "pointerout", (event) => {
    if (strip.contains(event.relatedTarget)) return;
    hovered = -1;
    sync();
  });
  on(strip, "pointercancel", () => {
    hovered = pinned = -1;
    sync();
  });
  on(strip, "focusin", (event) => {
    if (!event.target.matches(":focus-visible")) return;
    hovered = -1;
    focused = indexOf(event.target);
    sync();
  });
  on(strip, "focusout", () => {
    focused = -1;
    sync();
  });
  on(strip, "click", (event) => {
    if (event.pointerType !== "touch" && event.detail !== 0) return;
    const index = indexOf(event.target);
    if (index < 0) return;
    hovered = -1;
    pinned = pinned === index ? -1 : index;
    sync();
  });
  on(doc, "visibilitychange", () => {
    if (doc.hidden) {
      hovered = -1;
      sync();
    }
  });
  on(browser.matchMedia("(prefers-reduced-motion: reduce)"), "change", sync);
  rebuild();
  return {
    ...scope.controller,
    update(next) {
      if (destroyed) return;
      const previousImages = options.images;
      options = { ...options, ...next };
      scope.controller.update(next);
      if (options.images !== previousImages) rebuild();
      else {
        applyLayout();
        sync();
      }
    },
    destroy() {
      if (destroyed) return;
      destroyed = true;
      scope.controller.destroy();
    }
  };
}

// src/lib/motion-core/image-accordion-entry.js
/*!
MIT License

Copyright (c) 2026 Lorenzo Cabra

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
function mountImageAccordion(host, options = {}) {
  return createImageAccordion(host, options, "/* Image accordion \u2014 adapted from Bencho by Lorenzo Cabra (MIT). */\r\n.acc-scene {\r\n  display: grid;\r\n  min-height: var(--motion-height, 300px);\r\n  place-items: center;\r\n  background: transparent;\r\n}\r\n\r\n.acc {\r\n  display: flex;\r\n  align-items: stretch;\r\n  width: 100%;\r\n  height: 300px;\r\n  overflow: hidden;\r\n  border-radius: var(--acc-radius);\r\n  user-select: none;\r\n  touch-action: pan-y;\r\n}\r\n\r\n.acc-cell {\r\n  position: relative;\r\n  flex-basis: 0;\r\n  flex-shrink: 1;\r\n  min-width: 0;\r\n  overflow: hidden;\r\n  border: 0;\r\n  border-radius: var(--acc-radius);\r\n  padding: 0;\r\n  background: var(--muted, #ddd);\r\n  cursor: pointer;\r\n}\r\n\r\n.acc-cell img {\r\n  display: block;\r\n  width: 100%;\r\n  height: 100%;\r\n  /* Constant height keeps scale stable as the cell reveals more of the image. */\r\n  object-fit: cover;\r\n  pointer-events: none;\r\n  -webkit-user-drag: none;\r\n}\r\n\r\n.acc-cell:focus-visible {\r\n  outline: 2px solid var(--ring, #fff);\r\n  outline-offset: -3px;\r\n}\r\n", [{ name: '晨雾', src: '/static/img/bg01.webp' }, { name: '远山', src: '/static/img/bg02.webp' }, { name: '晚风', src: '/static/img/bg03.webp' }, { name: '晴昼', src: '/static/img/bg04.webp' }, { name: '归途', src: '/static/img/bg06.webp' }]);
}
export {
  mountImageAccordion
};
