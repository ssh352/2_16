package ch.algotrader.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

/**
 * A Hibernate UserType for Java5 enumerations. Taken from
 * <a href="http://www.hibernate.org/272.html">Java 5 EnumUserType</a>.
 */
public class HibernateEnumType implements EnhancedUserType, ParameterizedType
{
    @SuppressWarnings("unchecked")
    private Class<Enum> enumClass;

    /**
     * @see org.hibernate.usertype.ParameterizedType#setParameterValues(java.util.Properties)
     */
    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties parameters)
    {
        final String enumClassName = parameters.getProperty("enumClassName");
        try
        {
            //noinspection unchecked
            this.enumClass = (Class<Enum>)Class.forName(enumClassName);
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, Object)
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException
    {
        return cached;
    }

    /**
     * @see org.hibernate.usertype.UserType#deepCopy(Object)
     */
    public Object deepCopy(Object value) throws HibernateException
    {
        return value;
    }

    /**
     * @see org.hibernate.usertype.UserType#disassemble(Object)
     */
    @SuppressWarnings("unchecked")
    public Serializable disassemble(Object value) throws HibernateException
    {
        return (Enum)value;
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(Object, Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException
    {
        return x == y;
    }

    /**
     * @see org.hibernate.usertype.UserType#hashCode(Object)
     */
    public int hashCode(Object x) throws HibernateException
    {
        return x.hashCode();
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable()
    {
        return false;
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, String[], Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException
    {
        final String name = resultSet.getString(names[0]);
        return resultSet.wasNull() ? null : Enum.valueOf(this.enumClass, name);
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, Object, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index) throws HibernateException, SQLException
    {
        if (value == null)
        {
            statement.setNull(index, Types.VARCHAR);
        }
        else
        {
            if(value instanceof Enum)
            {
                statement.setString(index, ((Enum)value).name());
            }
            else
            {
                statement.setString(index, (String)value);
            }
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, String[], Object)
     */
    public Object nullSafeGet(ResultSet resultSet, String[] names,
        SessionImplementor session, Object owner) throws HibernateException, SQLException
    {
        return this.nullSafeGet(resultSet, names, owner);
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, Object, int)
     */
    public void nullSafeSet(
        PreparedStatement preparedStatement,
        Object data,
        int index,
        SessionImplementor session)
        throws HibernateException, SQLException
    {
        this.nullSafeSet(preparedStatement, data, index);
    }

    /**
     * @see org.hibernate.usertype.UserType#replace(Object, Object, Object)
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException
    {
        return original;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @SuppressWarnings("unchecked")
    public Class returnedClass()
    {
        return this.enumClass;
    }

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes()
    {
        return new int[]{Types.VARCHAR};
    }

    /**
     * @see org.hibernate.usertype.EnhancedUserType#fromXMLString(String)
     */
    @SuppressWarnings("unchecked")
    public Object fromXMLString(String xmlValue)
    {
        return Enum.valueOf(this.enumClass, xmlValue);
    }

    /**
     * @see org.hibernate.usertype.EnhancedUserType#objectToSQLString(Object)
     */
    @SuppressWarnings("unchecked")
    public String objectToSQLString(Object value)
    {
        return '\'' + ((Enum)value).name() + '\'';
    }

    /**
     * @see org.hibernate.usertype.EnhancedUserType#toXMLString(Object)
     */
    @SuppressWarnings("unchecked")
    public String toXMLString(Object value)
    {
        return ((Enum)value).name();
    }
}
