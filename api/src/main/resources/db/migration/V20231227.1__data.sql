INSERT INTO public.filter (id, name, selection, created_at)
VALUES (NEXTVAL('filter_id_seq'), 'First', 'SELECTION_2', now());

INSERT INTO public.criteria (id, filter_id, criteria_name, criteria_operator, criteria_value, created_at)
VALUES
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 'AMOUNT', 'EQUAL', '1', now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 'TITLE', 'STARTS_WITH', 'Meow', now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 'DATE', 'TO', '01/01/2024', now());

INSERT INTO public.filter (id, name, selection, created_at)
VALUES (NEXTVAL('filter_id_seq'), 'Second', 'SELECTION_3', now());

INSERT INTO public.criteria (id, filter_id, criteria_name, criteria_operator, criteria_value, created_at)
VALUES
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 'AMOUNT', 'LESS_THAN', '8', now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 'TITLE', 'EQUAL', 'aa', now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 'DATE', 'FROM', '01/10/2024', now());
