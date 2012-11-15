USE kwanza;
GO
/****** Object:  Table [dbo].[test_table]    Script Date: 11/30/2011 15:58:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

/******   TestEntity(test_entity) ******/
CREATE TABLE [dbo].[test_entity](
	[id] [int] NOT NULL,
	[int_field] [int] NULL,
	[string_field] [varchar](255)  NULL,
	[date_field] [datetime] null,
	[short_field] [int] null,
	[version] [int] not NULL,
	[entity_aid] [int]  null,
	[entity_bid] [int]  null,
	[entity_cid] [int]  null,
	[entity_did] [int]  null

 CONSTRAINT [PK_test_entity] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/******   TestEntityA(test_entity_a) ******/
CREATE TABLE [dbo].[test_entity_a](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL

 CONSTRAINT [PK_test_entity_a] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/******   TestEntityB(test_entity_b) ******/
CREATE TABLE [dbo].[test_entity_b](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL

 CONSTRAINT [PK_test_entity_b] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/******   TestEntityC(test_entity_c) ******/
CREATE TABLE [dbo].[test_entity_c](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL,
    [entity_eid] [int]  null,
    [entity_fid] [int]  null,

 CONSTRAINT [PK_test_entity_c] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/******   TestEntityD(test_entity_d) ******/
CREATE TABLE [dbo].[test_entity_d](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL

 CONSTRAINT [PK_test_entity_d] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/******   TestEntityE(test_entity_e) ******/
CREATE TABLE [dbo].[test_entity_e](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL,
    [entity_gid] [int]  null

 CONSTRAINT [PK_test_entity_e] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/******   TestEntityF(test_entity_f) ******/
CREATE TABLE [dbo].[test_entity_f](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL

 CONSTRAINT [PK_test_entity_f] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/******   TestEntityG(test_entity_g) ******/
CREATE TABLE [dbo].[test_entity_g](
	[id] [int] NOT NULL,
	[title] [varchar](255)  NULL,
	[version] [int] not NULL

 CONSTRAINT [PK_test_entity_g] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


SET ANSI_PADDING OFF;