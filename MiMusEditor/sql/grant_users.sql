-- Grants annotator users with the permissions needed to use the MiMus application.
-- Ideally, only the minimum set of permissions should be provided.
GRANT UPDATE ON Mimus.Document TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.HasMateria TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.Entity TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Bibliografia TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Artista TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Casa TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.GenereLiterari TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Instrument TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Lloc TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Ofici TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON Mimus.Promotor TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.EntityInstance TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.Transcription TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.Referencia TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.Relation TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.TeOfici TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.TeCasa TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.ServeixA TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.ResideixA TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON Mimus.Moviment TO mimus01@localhost;