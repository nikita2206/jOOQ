/*
 * This file is generated by jOOQ.
 */
package org.jooq.meta.postgres.pg_catalog.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.meta.postgres.pg_catalog.Keys;
import org.jooq.meta.postgres.pg_catalog.PgCatalog;
import org.jooq.meta.postgres.pg_catalog.tables.PgClass.PgClassPath;
import org.jooq.meta.postgres.pg_catalog.tables.PgNamespace.PgNamespacePath;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PgConstraint extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>pg_catalog.pg_constraint</code>
     */
    public static final PgConstraint PG_CONSTRAINT = new PgConstraint();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>pg_catalog.pg_constraint.oid</code>.
     */
    public final TableField<Record, Long> OID = createField(DSL.name("oid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conname</code>.
     */
    public final TableField<Record, String> CONNAME = createField(DSL.name("conname"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.connamespace</code>.
     */
    public final TableField<Record, Long> CONNAMESPACE = createField(DSL.name("connamespace"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.contype</code>.
     */
    public final TableField<Record, String> CONTYPE = createField(DSL.name("contype"), SQLDataType.CHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.condeferrable</code>.
     */
    public final TableField<Record, Boolean> CONDEFERRABLE = createField(DSL.name("condeferrable"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.condeferred</code>.
     */
    public final TableField<Record, Boolean> CONDEFERRED = createField(DSL.name("condeferred"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.convalidated</code>.
     */
    public final TableField<Record, Boolean> CONVALIDATED = createField(DSL.name("convalidated"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conrelid</code>.
     */
    public final TableField<Record, Long> CONRELID = createField(DSL.name("conrelid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.contypid</code>.
     */
    public final TableField<Record, Long> CONTYPID = createField(DSL.name("contypid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conindid</code>.
     */
    public final TableField<Record, Long> CONINDID = createField(DSL.name("conindid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conparentid</code>.
     */
    public final TableField<Record, Long> CONPARENTID = createField(DSL.name("conparentid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.confrelid</code>.
     */
    public final TableField<Record, Long> CONFRELID = createField(DSL.name("confrelid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.confupdtype</code>.
     */
    public final TableField<Record, String> CONFUPDTYPE = createField(DSL.name("confupdtype"), SQLDataType.CHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.confdeltype</code>.
     */
    public final TableField<Record, String> CONFDELTYPE = createField(DSL.name("confdeltype"), SQLDataType.CHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.confmatchtype</code>.
     */
    public final TableField<Record, String> CONFMATCHTYPE = createField(DSL.name("confmatchtype"), SQLDataType.CHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conislocal</code>.
     */
    public final TableField<Record, Boolean> CONISLOCAL = createField(DSL.name("conislocal"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.coninhcount</code>.
     */
    public final TableField<Record, Integer> CONINHCOUNT = createField(DSL.name("coninhcount"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.connoinherit</code>.
     */
    public final TableField<Record, Boolean> CONNOINHERIT = createField(DSL.name("connoinherit"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conkey</code>.
     */
    public final TableField<Record, Short[]> CONKEY = createField(DSL.name("conkey"), SQLDataType.SMALLINT.array(), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.confkey</code>.
     */
    public final TableField<Record, Short[]> CONFKEY = createField(DSL.name("confkey"), SQLDataType.SMALLINT.array(), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conpfeqop</code>.
     */
    public final TableField<Record, Long[]> CONPFEQOP = createField(DSL.name("conpfeqop"), SQLDataType.BIGINT.array(), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conppeqop</code>.
     */
    public final TableField<Record, Long[]> CONPPEQOP = createField(DSL.name("conppeqop"), SQLDataType.BIGINT.array(), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conffeqop</code>.
     */
    public final TableField<Record, Long[]> CONFFEQOP = createField(DSL.name("conffeqop"), SQLDataType.BIGINT.array(), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.confdelsetcols</code>.
     */
    public final TableField<Record, Short[]> CONFDELSETCOLS = createField(DSL.name("confdelsetcols"), SQLDataType.SMALLINT.array(), this, "");

    /**
     * The column <code>pg_catalog.pg_constraint.conexclop</code>.
     */
    public final TableField<Record, Long[]> CONEXCLOP = createField(DSL.name("conexclop"), SQLDataType.BIGINT.array(), this, "");

    /**
     * @deprecated Unknown data type. If this is a qualified, user-defined type,
     * it may have been excluded from code generation. If this is a built-in
     * type, you can define an explicit {@link org.jooq.Binding} to specify how
     * this type should be handled. Deprecation can be turned off using
     * {@literal <deprecationOnUnknownTypes/>} in your code generator
     * configuration.
     */
    @Deprecated
    public final TableField<Record, Object> CONBIN = createField(DSL.name("conbin"), org.jooq.impl.DefaultDataType.getDefaultDataType("\"pg_catalog\".\"pg_node_tree\""), this, "");

    private PgConstraint(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private PgConstraint(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>pg_catalog.pg_constraint</code> table reference
     */
    public PgConstraint(String alias) {
        this(DSL.name(alias), PG_CONSTRAINT);
    }

    /**
     * Create an aliased <code>pg_catalog.pg_constraint</code> table reference
     */
    public PgConstraint(Name alias) {
        this(alias, PG_CONSTRAINT);
    }

    /**
     * Create a <code>pg_catalog.pg_constraint</code> table reference
     */
    public PgConstraint() {
        this(DSL.name("pg_constraint"), null);
    }

    public <O extends Record> PgConstraint(Table<O> path, ForeignKey<O, Record> childPath, InverseForeignKey<O, Record> parentPath) {
        super(path, childPath, parentPath, PG_CONSTRAINT);
    }

    public static class PgConstraintPath extends PgConstraint implements Path<Record> {
        public <O extends Record> PgConstraintPath(Table<O> path, ForeignKey<O, Record> childPath, InverseForeignKey<O, Record> parentPath) {
            super(path, childPath, parentPath);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : PgCatalog.PG_CATALOG;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.PG_CONSTRAINT_OID_INDEX;
    }

    @Override
    public List<UniqueKey<Record>> getUniqueKeys() {
        return Arrays.asList(Keys.PG_CONSTRAINT_CONRELID_CONTYPID_CONNAME_INDEX);
    }

    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.asList(Keys.PG_CONSTRAINT__SYNTHETIC_FK_PG_CONSTRAINT__SYNTHETIC_PK_PG_NAMESPACE, Keys.PG_CONSTRAINT__SYNTHETIC_FK_PG_CONSTRAINT__SYNTHETIC_PK_PG_CLASS);
    }

    private transient PgNamespacePath _pgNamespace;

    /**
     * Get the implicit join path to the <code>pg_catalog.pg_namespace</code>
     * table.
     */
    public PgNamespacePath pgNamespace() {
        if (_pgNamespace == null)
            _pgNamespace = new PgNamespacePath(this, Keys.PG_CONSTRAINT__SYNTHETIC_FK_PG_CONSTRAINT__SYNTHETIC_PK_PG_NAMESPACE, null);

        return _pgNamespace;
    }

    private transient PgClassPath _pgClass;

    /**
     * Get the implicit join path to the <code>pg_catalog.pg_class</code> table.
     */
    public PgClassPath pgClass() {
        if (_pgClass == null)
            _pgClass = new PgClassPath(this, Keys.PG_CONSTRAINT__SYNTHETIC_FK_PG_CONSTRAINT__SYNTHETIC_PK_PG_CLASS, null);

        return _pgClass;
    }

    @Override
    public PgConstraint as(String alias) {
        return new PgConstraint(DSL.name(alias), this);
    }

    @Override
    public PgConstraint as(Name alias) {
        return new PgConstraint(alias, this);
    }

    @Override
    public PgConstraint as(Table<?> alias) {
        return new PgConstraint(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public PgConstraint rename(String name) {
        return new PgConstraint(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PgConstraint rename(Name name) {
        return new PgConstraint(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public PgConstraint rename(Table<?> name) {
        return new PgConstraint(name.getQualifiedName(), null);
    }
}
