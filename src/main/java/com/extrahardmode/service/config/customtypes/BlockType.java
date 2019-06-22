package com.extrahardmode.service.config.customtypes;


import com.extrahardmode.service.RegexHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Holds one blocktype, but a range of metadata for that block.
 * F.e. this could have meta for spruce, oak and jungle wood, but exclude birch.
 *
 * @deprecated No longer needed with "The Flattening" in 1.13
 *
 * @author Diemex
 */
@Deprecated
public final class BlockType
{
    private static Pattern separators = Pattern.compile("[^A-Za-z0-9_]");
    private int blockId = -1;
    private Set<Short> meta = new LinkedHashSet<Short>();


    public BlockType(int blockId)
    {
        this.blockId = blockId;
    }


    public BlockType(Material mat, Short... meta)
    {
        if (!mat.isLegacy())
            return;
        this.blockId = mat.getId();
        Collections.addAll(this.meta, meta);
    }


    public BlockType(int blockId, Short... meta)
    {
        this.blockId = blockId;
        Collections.addAll(this.meta, meta);
    }


    public BlockType(int blockId, short meta)
    {
        this.blockId = blockId;
        this.meta.add(meta);
    }


    public BlockType(int blockId, Collection<Short> meta)
    {
        this.blockId = blockId;
        this.meta.addAll(meta);
    }


    public int getBlockId()
    {
        return blockId;
    }


    public Set<Short> getAllMeta()
    {
        return new HashSet<Short>(meta);
    }


    public byte getByteMeta()
    {
        return meta.size() > 0 ? (byte) RegexHelper.safeCast(meta.iterator().next(), Byte.MIN_VALUE, Byte.MAX_VALUE) : 0;
    }


    public short getMeta()
    {
        return meta.size() > 0 ? meta.iterator().next() : 0;
    }


    private boolean matchesMeta(short meta)
    {
        if (this.meta.size() > 0)
        {
            for (Short aMeta : this.meta)
            {
                if (aMeta == meta)
                    return true;
            }
        } else //no meta specified -> all blocks match
            return true;
        return false;
    }


    public boolean matches(int blockId)
    {
        return this.blockId == blockId;
    }


    public boolean matches(int blockId, short meta)
    {
        return matches(blockId) && matchesMeta(meta);
    }


    public boolean matches(Block block)
    {
        return matches(block.getType().getId(), block.getData());
    }


    public boolean matches(ItemStack stack)
    {
        return matches(stack.getType().getId(), stack.getData().getData());
    }


    public String saveToString()
    {
        StringBuilder builder = new StringBuilder();
        Material material = getMaterial(blockId);
        builder.append(material != null ? material.name() : blockId);

        boolean first = true;
        for (Short metaBit : meta)
        {
            if (first) builder.append('@');
            else builder.append(',');
            builder.append(metaBit);
            if (first) first = false;
        }

        return builder.toString();
    }

    //Temporary thing for for 1.13 compatibility
    //TODO: remove when properly supporting 1.13 (if there's even a use for this package at this point)
    private Material getMaterial(int id)
    {
        for (Material material : Material.values())
            if (material.getId() == id)
                return material;
        return null;
    }
    public Material getType()
    {
        return getMaterial(blockId);
    }


    public boolean isValid()
    {
        return blockId >= 0;
    }


    @Override
    public String toString()
    {
        return saveToString();
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (!(obj instanceof BlockType))
            return false;
        return blockId == ((BlockType) obj).blockId &&
                meta.equals(((BlockType) obj).meta);
    }


    @Override
    public int hashCode()
    {
        int hash = blockId;
        for (short data : meta)
            hash += data;
        return hash;
    }
}
