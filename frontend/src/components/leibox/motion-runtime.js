/**
 * LeiBox 共享动效运行时 —— Vue 2 / Babel 6 移植版
 * 原始来源：https://leibox.pantoria.cn 的 motion-runtime.ts
 * 移植改动：剥离 TypeScript 类型；降级可选链、空值合并、String.matchAll；
 *          用工厂函数替代 class extends 原生构造器，避免 Babel 6 转译后无法 new 原生类。
 * 组件源码内的声明与设计参考署名均予保留。
 */
const fonts = new Map();

export function createSurface(target, config, initial) {
    let options = Object.assign({}, initial || {});
    let destroyed = false;
    let scale = 1;
    const owner = target.ownerDocument;
    const browser = owner.defaultView;
    const element = owner.createElement('div');
    element.dataset.motionSurface = '';
    element.style.cssText = 'display:block;position:relative;width:100%;min-width:0;overflow:hidden;isolation:isolate;';
    target.appendChild(element);

    const root = element.attachShadow({ mode: 'open' });
    const style = owner.createElement('style');
    const baseCss = ':host{display:block}[hidden]{display:none!important}*{box-sizing:border-box}' +
        '.motion-frame{position:relative;transform-origin:top left;overflow:hidden}' +
        '.motion-body{position:relative;width:100%;min-height:100%;margin:0}' +
        ':host([data-reduced-motion=true]) *{animation-duration:0s!important;animation-iteration-count:1!important;transition-duration:0s!important}';
    style.textContent = baseCss + config.css
        .replace(/(^|[,\s}>+~])(?:html|body)(?=[\s,{.#:[>])/g, '$1.motion-body')
        .replace(/:root\b/g, '.motion-body')
        .replace(/100(?:s|d|l)?vh/g, 'var(--motion-height)')
        .replace(/position\s*:\s*fixed/g, 'position:absolute');

    const frame = owner.createElement('div');
    frame.className = 'motion-frame';
    const body = owner.createElement('div');
    body.className = 'motion-body';
    body.innerHTML = config.markup;
    frame.appendChild(body);
    root.appendChild(style);
    root.appendChild(frame);

    const frames = new Set();
    const deferredFrames = new Set();
    const timeouts = new Set();
    const intervals = new Set();
    const observers = new Set();
    const cleanups = [];
    let painted = false;

    const fontRule = /@font-face\s*\{([^}]+)\}/g;
    let fontMatch;
    while ((fontMatch = fontRule.exec(config.css)) !== null) {
        const nameMatch = fontMatch[1].match(/font-family:\s*["']?([^;"']+)/);
        const sourceMatch = fontMatch[1].match(/src:\s*(url\([^)]*\))/);
        const name = nameMatch && nameMatch[1] ? nameMatch[1].trim() : null;
        const source = sourceMatch ? sourceMatch[1] : null;
        if (!name || !source) continue;
        const key = name + ':' + source;
        let cached = fonts.get(key);
        if (!cached) {
            const face = new FontFace(name, source, { weight: '100 900' });
            owner.fonts.add(face);
            face.load().catch(function () { });
            cached = { face: face, users: 0 };
            fonts.set(key, cached);
        }
        cached.users++;
        cleanups.push(function () {
            const entry = fonts.get(key);
            if (entry && --entry.users === 0) {
                owner.fonts.delete(entry.face);
                fonts.delete(key);
            }
        });
    }

    const ready = [];
    const listeners = [];
    const on = function (eventTarget, type, listener, settings) {
        eventTarget.addEventListener(type, listener, settings);
        listeners.push({ target: eventTarget, type: type, listener: listener, options: settings });
    };
    const requestFrame = function (callback) {
        if (destroyed) return 0;
        if (owner.hidden || (painted && reduced())) {
            deferredFrames.add(callback);
            return 0;
        }
        const id = browser.requestAnimationFrame(function (time) {
            frames.delete(id);
            painted = true;
            if (!destroyed) callback(time);
        });
        frames.add(id);
        return id;
    };
    const cancelFrame = function (id) {
        browser.cancelAnimationFrame(id);
        frames.delete(id);
    };
    const timeout = function (callback, ms) {
        const id = browser.setTimeout(function () {
            timeouts.delete(id);
            if (!destroyed) callback();
        }, ms);
        timeouts.add(id);
        return id;
    };
    const interval = function (callback, ms) {
        const id = browser.setInterval(function () {
            if (!destroyed) callback();
        }, ms);
        intervals.add(id);
        return id;
    };
    const clearTimeout = function (id) { browser.clearTimeout(id); timeouts.delete(id); };
    const clearInterval = function (id) { browser.clearInterval(id); intervals.delete(id); };
    const query = function (selector) {
        return selector === 'body' || selector === 'html' ? body : root.querySelector(selector);
    };
    const document = new Proxy(owner, {
        get: function (original, key) {
            if (key === 'body' || key === 'documentElement') return body;
            if (key === 'activeElement') return root.activeElement;
            if (key === 'getElementById') return function (id) { return root.getElementById(id); };
            if (key === 'querySelector') return query;
            if (key === 'querySelectorAll') return function (selector) { return root.querySelectorAll(selector); };
            if (key === 'addEventListener') return function (type, listener, settings) {
                if (type === 'DOMContentLoaded') ready.push(listener);
                else on(owner, type, listener, settings);
            };
            if (key === 'removeEventListener') return owner.removeEventListener.bind(owner);
            const value = Reflect.get(original, key, original);
            return typeof value === 'function' ? value.bind(original) : value;
        }
    });
    const reduced = function () {
        return options.reducedMotion != null
            ? options.reducedMotion
            : browser.matchMedia('(prefers-reduced-motion: reduce)').matches;
    };
    const resume = function () {
        if (!owner.hidden && !reduced()) {
            const callbacks = Array.from(deferredFrames);
            deferredFrames.clear();
            callbacks.forEach(requestFrame);
        }
    };
    on(owner, 'visibilitychange', resume);
    const matchMedia = function (q) {
        const media = browser.matchMedia(q);
        return new Proxy(media, {
            get: function (original, key) {
                if (key === 'matches' && q.indexOf('prefers-reduced-motion') >= 0) return reduced();
                if (key === 'addEventListener') return function (type, listener) { return on(media, type, listener); };
                const value = Reflect.get(original, key, original);
                return typeof value === 'function' ? value.bind(original) : value;
            }
        });
    };
    const window = new Proxy(browser, {
        get: function (original, key) {
            if (key === 'document') return document;
            if (key === 'innerWidth') return frame.offsetWidth;
            if (key === 'innerHeight') return config.height;
            if (key === 'requestAnimationFrame') return requestFrame;
            if (key === 'cancelAnimationFrame') return cancelFrame;
            if (key === 'setTimeout') return timeout;
            if (key === 'clearTimeout') return clearTimeout;
            if (key === 'setInterval') return interval;
            if (key === 'clearInterval') return clearInterval;
            if (key === 'matchMedia') return matchMedia;
            if (key === 'addEventListener') return function (type, listener, settings) {
                return on(browser, type, listener, settings);
            };
            const value = Reflect.get(original, key, original);
            return typeof value === 'function' ? value.bind(original) : value;
        }
    });
    const resize = function () {
        const available = element.clientWidth || config.width;
        scale = config.responsive ? 1 : Math.min(1, available / config.width);
        const height = options.height != null ? options.height : config.height;
        element.style.setProperty('--motion-height', height + 'px');
        frame.style.width = (config.responsive ? available : Math.max(config.width, available)) + 'px';
        frame.style.height = config.responsive ? 'auto' : height + 'px';
        frame.style.minHeight = config.responsive ? height + 'px' : '0';
        frame.style.transform = 'scale(' + scale + ')';
        body.style.minHeight = height + 'px';
        element.style.height = config.responsive ? 'auto' : (height * scale) + 'px';
        element.dataset.reducedMotion = String(reduced());
    };
    const resizeObserver = new browser.ResizeObserver(resize);
    resizeObserver.observe(element);
    resize();
    const TrackedResizeObserver = function (callback) {
        const observer = new browser.ResizeObserver(callback);
        observers.add(observer);
        return observer;
    };
    const TrackedIntersectionObserver = function (callback, init) {
        const observer = new browser.IntersectionObserver(callback, init);
        observers.add(observer);
        return observer;
    };
    const controller = {
        element: element,
        root: root,
        update: function (next) {
            if (destroyed) return;
            options = Object.assign({}, options, next);
            resize();
            resume();
        },
        destroy: function () {
            if (destroyed) return;
            destroyed = true;
            frames.forEach(function (id) { browser.cancelAnimationFrame(id); });
            timeouts.forEach(function (id) { browser.clearTimeout(id); });
            intervals.forEach(function (id) { browser.clearInterval(id); });
            resizeObserver.disconnect();
            observers.forEach(function (observer) { observer.disconnect(); });
            listeners.forEach(function (item) {
                item.target.removeEventListener(item.type, item.listener, item.options);
            });
            cleanups.reverse().forEach(function (cleanup) { cleanup(); });
            root.querySelectorAll('dialog[open]').forEach(function (dialog) { dialog.close(); });
            element.remove();
            frames.clear();
            deferredFrames.clear();
            timeouts.clear();
            intervals.clear();
        }
    };
    return Object.assign({}, controller, {
        controller: controller,
        body: body,
        frame: frame,
        document: document,
        window: window,
        on: on,
        reduced: reduced,
        requestAnimationFrame: requestFrame,
        cancelAnimationFrame: cancelFrame,
        setTimeout: timeout,
        clearTimeout: clearTimeout,
        setInterval: interval,
        clearInterval: clearInterval,
        ResizeObserver: TrackedResizeObserver,
        IntersectionObserver: TrackedIntersectionObserver,
        matchMedia: matchMedia,
        addCleanup: function (cleanup) { cleanups.push(cleanup); },
        ready: function () {
            ready.splice(0).forEach(function (listener) {
                const event = new Event('DOMContentLoaded');
                if (typeof listener === 'function') listener.call(document, event);
                else listener.handleEvent(event);
            });
        },
        point: function (event) {
            const rect = frame.getBoundingClientRect();
            return { x: (event.clientX - rect.x) / scale, y: (event.clientY - rect.y) / scale };
        },
        rect: function (node) {
            const rect = node.getBoundingClientRect();
            const origin = frame.getBoundingClientRect();
            return {
                x: (rect.x - origin.x) / scale, y: (rect.y - origin.y) / scale,
                left: (rect.left - origin.left) / scale, top: (rect.top - origin.top) / scale,
                right: (rect.right - origin.left) / scale, bottom: (rect.bottom - origin.top) / scale,
                width: rect.width / scale, height: rect.height / scale
            };
        }
    });
}
