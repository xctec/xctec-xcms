package com.df4j.xctec.xcms.core.tree;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 树路径组件（@Embeddable）。
 *
 * - path：祖先 id 拼接（不含自身），以 '/' 包围，用于 SQL 下钻。示例：/0/1/1001/
 * - codePath：父的 codePath + 自身 code（含自身），以 '/' 包围，用于日志可读/导入导出。示例：/HQ/EAST/SH/
 * - level：层级，根为 1。
 *
 * 两者均以 '/' 包围，避免 LIKE '/1/10%' 误匹配 '/1/100'。
 * @Embeddable 会生成 QTreePath，在实体 Q 类中以 tenant.treePath.path 形式访问。
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreePath implements Serializable {

    @Column(name = "path", nullable = false, length = 1024)
    private String path;

    @Column(name = "code_path", nullable = false, length = 2048)
    private String codePath;

    @Column(name = "level", nullable = false)
    private Integer level;
}
