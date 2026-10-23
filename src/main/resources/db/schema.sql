CREATE TABLE job_application (
                                 id UUID PRIMARY KEY,
                                 business_key TEXT NOT NULL UNIQUE,
                                 status TEXT NOT NULL,
                                 title TEXT NOT NULL,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                 CONSTRAINT job_application_status_known
                                     CHECK (
                                         status IN (
                                                    'DRAFT',
                                                    'SUBMITTED',
                                                    'APPROVED',
                                                    'REJECTED'
                                             )
                                         )
);