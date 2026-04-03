CREATE KEYSPACE IF NOT EXISTS chat_memory
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
