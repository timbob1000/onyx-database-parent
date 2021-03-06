package com.onyx.diskmap.node;

/**
 * Created by tosborn1 on 1/10/17.
 *
 * This class indicates both a bitmap and skip list combined node
 *
 * @since 1.2.0
 */
public class CombinedIndexHashMatrixNode {

    public final HashMatrixNode bitMapNode;
    public final int hashDigit;
    public SkipListHeadNode head;

    public CombinedIndexHashMatrixNode(final SkipListHeadNode base, final HashMatrixNode node, int hashDigit) {
        this.head = base;
        this.bitMapNode = node;
        this.hashDigit = hashDigit;
    }

    @Override
    public int hashCode()
    {
        return (int)(this.bitMapNode.position ^ (this.bitMapNode.position >>> 32));
    }

    @Override
    public boolean equals(Object object)
    {
        return (object instanceof CombinedIndexHashMatrixNode && ((CombinedIndexHashMatrixNode) object).bitMapNode.position == bitMapNode.position);
    }

}
