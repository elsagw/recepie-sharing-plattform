CREATE TABLE IF NOT EXISTS saved_recipes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    recipe_id BIGINT NOT NULL REFERENCES external_recipes(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_saved_recipes_user_recipe UNIQUE (user_id, recipe_id)
);

CREATE INDEX IF NOT EXISTS idx_saved_recipes_user_created_at
    ON saved_recipes (user_id, created_at DESC);