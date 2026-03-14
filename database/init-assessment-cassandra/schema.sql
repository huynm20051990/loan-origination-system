CREATE KEYSPACE IF NOT EXISTS assessment_keyspace
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};