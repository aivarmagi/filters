INSERT INTO public.filter (id, name, selection)
VALUES (NEXTVAL('filter_id_seq'), 'First', 'SELECTION_2');

INSERT INTO public.criteria (id, filter_id, amount, title, date)
VALUES
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 2, null, null),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), null, 'Meow', null),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), null, null, now());

INSERT INTO public.filter (id, name, selection)
VALUES (NEXTVAL('filter_id_seq'), 'Second', 'SELECTION_3');

INSERT INTO public.criteria (id, filter_id, amount, title, date)
VALUES
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 8, null, null),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), null, 'Woof!', null);
