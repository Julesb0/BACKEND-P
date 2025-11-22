-- Tabla de perfiles de usuario
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT NOT NULL UNIQUE,
    display_name TEXT NOT NULL,
    bio TEXT,
    location TEXT,
    company TEXT,
    role TEXT,
    linkedin_url TEXT,
    website TEXT,
    is_public BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Tabla de conexiones entre usuarios
CREATE TABLE IF NOT EXISTS connections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requester_id TEXT NOT NULL,
    addressee_id TEXT NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('pending', 'accepted', 'rejected')),
    message TEXT,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(requester_id, addressee_id)
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_profiles_user_id ON profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_profiles_is_public ON profiles(is_public);
CREATE INDEX IF NOT EXISTS idx_connections_requester_id ON connections(requester_id);
CREATE INDEX IF NOT EXISTS idx_connections_addressee_id ON connections(addressee_id);
CREATE INDEX IF NOT EXISTS idx_connections_status ON connections(status);

-- Habilitar RLS (Row Level Security)
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE connections ENABLE ROW LEVEL SECURITY;

-- Políticas para profiles
CREATE POLICY "Los usuarios pueden ver perfiles públicos" ON profiles
    FOR SELECT USING (is_public = true);

CREATE POLICY "Los usuarios pueden ver su propio perfil" ON profiles
    FOR SELECT USING (auth.uid()::text = user_id);

CREATE POLICY "Los usuarios pueden crear su propio perfil" ON profiles
    FOR INSERT WITH CHECK (auth.uid()::text = user_id);

CREATE POLICY "Los usuarios pueden actualizar su propio perfil" ON profiles
    FOR UPDATE USING (auth.uid()::text = user_id) WITH CHECK (auth.uid()::text = user_id);

-- Políticas para connections
CREATE POLICY "Los usuarios pueden ver sus propias conexiones" ON connections
    FOR SELECT USING (auth.uid()::text = requester_id OR auth.uid()::text = addressee_id);

CREATE POLICY "Los usuarios pueden crear conexiones" ON connections
    FOR INSERT WITH CHECK (auth.uid()::text = requester_id);

CREATE POLICY "Los usuarios pueden actualizar conexiones donde son parte" ON connections
    FOR UPDATE USING (auth.uid()::text = requester_id OR auth.uid()::text = addressee_id) 
    WITH CHECK (auth.uid()::text = requester_id OR auth.uid()::text = addressee_id);

-- Conceder permisos a los roles anon y authenticated
GRANT SELECT ON profiles TO anon, authenticated;
GRANT INSERT ON profiles TO anon, authenticated;
GRANT UPDATE ON profiles TO anon, authenticated;

GRANT SELECT ON connections TO anon, authenticated;
GRANT INSERT ON connections TO anon, authenticated;
GRANT UPDATE ON connections TO anon, authenticated;