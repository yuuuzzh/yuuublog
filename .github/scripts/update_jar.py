#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
用 CI 上传的 BOOT-INF/classes 增量重建 Spring Boot fat jar。

用法：python3 update_jar.py <jar 路径> <classes 目录>
  - classes 目录形如 /app/deploy/incoming/classes/yuuu-blog/BOOT-INF/classes
  - 逻辑：复制旧 jar，剔除旧的 BOOT-INF/classes/*，写入新 classes，原地替换
  - 依赖（BOOT-INF/lib）保持不变，因此不需要跨境上传 100MB+ 的 fat jar
"""
import os
import shutil
import sys
import zipfile

CLASSES_PREFIX = "BOOT-INF/classes/"


def collect(classes_dir):
    """返回 [(本地路径, jar 内路径)]，jar 内路径以 BOOT-INF/classes/ 开头"""
    base = os.path.dirname(classes_dir.rstrip("/"))  # .../BOOT-INF
    out = []
    for root, _, names in os.walk(classes_dir):
        for name in names:
            path = os.path.join(root, name)
            arc = os.path.relpath(path, base).replace(os.sep, "/")
            out.append((path, arc))
    return out


def main():
    if len(sys.argv) != 3:
        print("usage: update_jar.py <jar> <classes_dir>")
        return 2
    jar, classes_dir = sys.argv[1], sys.argv[2]
    if not os.path.isfile(jar):
        print("jar not found: %s" % jar)
        return 1
    if not os.path.isdir(classes_dir):
        print("classes dir not found: %s" % classes_dir)
        return 1
    # 目录结构必须是 .../BOOT-INF/classes，否则会把类写到 jar 的错误路径、直接破坏启动
    norm = classes_dir.rstrip("/").replace("\\", "/")
    if not norm.endswith("BOOT-INF/classes"):
        print("classes dir must end with BOOT-INF/classes, got: %s" % classes_dir)
        return 1

    new_files = collect(classes_dir)
    if not new_files:
        print("no class files collected, abort")
        return 1

    tmp = jar + ".tmp"
    src = zipfile.ZipFile(jar, "r")
    written = 0
    try:
        with zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as dst:
            # 1. 保留除 BOOT-INF/classes 以外的全部内容（依赖、MANIFEST、loader 等）
            for item in src.infolist():
                if item.filename.startswith(CLASSES_PREFIX):
                    continue
                dst.writestr(item, src.read(item.filename))
            # 2. 写入新的 classes
            for path, arc in new_files:
                dst.write(path, arc)
                written += 1
    finally:
        src.close()

    # 3. 校验后原地替换
    with zipfile.ZipFile(tmp, "r") as check:
        if check.testzip() is not None:
            os.remove(tmp)
            print("jar integrity check FAILED")
            return 1
    shutil.move(tmp, jar)
    print("updated %s: %s class/resource files written" % (jar, written))
    return 0


if __name__ == "__main__":
    sys.exit(main())
