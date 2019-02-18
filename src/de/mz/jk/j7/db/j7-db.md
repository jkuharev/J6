# Package info
Package j7.db wraps JDBC access to databases 
and provides some convenience functionality
while working with databases.

# Relationship to de.mz.jk.jsix.mysql
The majority of the code here was derived from `de.mz.jk.jsix.mysql.MySQL`.
The original class remains untouched so far for not breaking the compatibility
with software relying on it.

However, J7 implements the database access in a hierarchical way.
The future plan is to derive an new MySQL interface 
that will behave in the same way like the OracleDB implemented here.