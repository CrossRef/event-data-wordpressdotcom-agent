(ns event-data-wordpressdotcom-agent.wordpressdotcom-test
  (:require [clojure.test :refer :all]
            [event-data-wordpressdotcom-agent.wordpressdotcom :as wordpressdotcom]))

(deftest can-parse-response
  (testing "Can parse a Reddit response"
    (let [input (slurp "resources/test/new.json")
          result (wordpressdotcom/parse-page input)
          expected {:after-token "t3_4dl4nl",
                    :items [
                    {:url "http://dx.doi.org/10.1038/ncomms11825",
                    :id "4o0cl4",
                    :title "Personal momentary happiness is quantified, shown to be reduced by inequality and indicative of altruism",
                    :author "sataky",
                    :permalink "/r/science/comments/4o0cl4/personal_momentary_happiness_is_quantified_shown/",
                    :created_utc 1465890225,
                    :subwordpressdotcom "science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.7554/eLife.14175",
                    :id "4na8db",
                    :title "Flatworms left in sunlight spur investigations into porphyrias, rare metabolic disorders that can cause extreme sensitivity to light, facial hair growth, and hallucinations.",
                    :author "StuartRFKing",
                    :permalink "/r/science/comments/4na8db/flatworms_left_in_sunlight_spur_investigations/",
                    :created_utc 1465468787,
                    :subwordpressdotcom "science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1016/j.cub.2016.04.041",
                    :id "4n4chm",
                    :title "Obituary of Hans Meinhardt by Siegfried Roth",
                    :author "zorngibel",
                    :permalink "/r/biology/comments/4n4chm/obituary_of_hans_meinhardt_by_siegfried_roth/",
                    :created_utc 1465381631,
                    :subwordpressdotcom "biology",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1002/pro.2959",
                    :id "4n2l6r",
                    :title "Critique of XFEL crystallography's claims of radiation-free data",
                    :author "archehakadah",
                    :permalink "/r/Biophysics/comments/4n2l6r/critique_of_xfel_crystallographys_claims_of/",
                    :created_utc 1465349759,
                    :subwordpressdotcom "Biophysics",
                    :kind "t3"} 
                    {:url "https://dx.doi.org/10.6084/m9.figshare.3413821.v1",
                    :id "4muklv",
                    :title "Understanding the threat of excellence in the academy",
                    :author "junana",
                    :permalink "/r/Open_Science/comments/4muklv/understanding_the_threat_of_excellence_in_the/",
                    :created_utc 1465239043,
                    :subwordpressdotcom "Open_Science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1371/journal.ppat.1005651",
                    :id "4ko5n0",
                    :title "Bromocritpine and LSD identified as potential therapeutics for Schistosomiasis",
                    :author "zendudest",
                    :permalink "/r/neuroscience/comments/4ko5n0/bromocritpine_and_lsd_identified_as_potential/",
                    :created_utc 1464013293,
                    :subwordpressdotcom "neuroscience",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1111/acv.12106",
                    :id "4k6lqw",
                    :title "How poaching of an endangered species in their natural habitats could create ecological traps? the case of the Andean bears in the Cordillera de Mérida, Venezuela",
                    :author "jrfep",
                    :permalink "/r/antipoaching/comments/4k6lqw/how_poaching_of_an_endangered_species_in_their/",
                    :created_utc 1463718899,
                    :subwordpressdotcom "antipoaching",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1111/acv.12106",
                    :id "4k6kff",
                    :title "Combining threat and occurrence models to predict potential ecological traps for Andean bears in the Cordillera de Mérida, Venezuela",
                    :author "jrfep",
                    :permalink "/r/ecology/comments/4k6kff/combining_threat_and_occurrence_models_to_predict/",
                    :created_utc 1463718285,
                    :subwordpressdotcom "ecology",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1002/da.22466",
                    :id "4jziwu",
                    :title "Pitt study finds strong correlation between depression and social media use in young adults.",
                    :author "YOJIBBAJABBA",
                    :permalink "/r/SanctionedSuicide/comments/4jziwu/pitt_study_finds_strong_correlation_between/",
                    :created_utc 1463612864,
                    :subwordpressdotcom "SanctionedSuicide",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1093/scan/nsw057",
                    :id "4j22zk",
                    :title "Evidence Acetaminophen (paracetamol) may reduce empathy. (Reasonable sample, results marginal)",
                    :author "bigmansam45",
                    :permalink "/r/science/comments/4j22zk/evidence_acetaminophen_paracetamol_may_reduce/",
                    :created_utc 1463077424,
                    :subwordpressdotcom "science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1371/journal.pbio.1002456",
                    :id "4j1yuf",
                    :title "Badges to Acknowledge Open Practices: A Simple, Low-Cost, Effective Method for Increasing Transparency",
                    :author "rollem",
                    :permalink "/r/science/comments/4j1yuf/badges_to_acknowledge_open_practices_a_simple/",
                    :created_utc 1463076035,
                    :subwordpressdotcom "science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1063/PT.3.3079",
                    :id "4ih6ri",
                    :title "Physics Today - The bicentennial of Francis Ronalds's electric telegraph",
                    :author "eugenemah",
                    :permalink "/r/amateurradio/comments/4ih6ri/physics_today_the_bicentennial_of_francis/",
                    :created_utc 1462754296,
                    :subwordpressdotcom "amateurradio",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1063/PT.3.3155",
                    :id "4icppz",
                    :title "A nonreciprocal antenna speaks without listening",
                    :author "eugenemah",
                    :permalink "/r/amateurradio/comments/4icppz/a_nonreciprocal_antenna_speaks_without_listening/",
                    :created_utc 1462671303,
                    :subwordpressdotcom "amateurradio",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1371/journal.pone.0153419",
                    :id "4hyjkp",
                    :title "Scientists identify common thread uniting Trump supporters.",
                    :author "ReckonerA",
                    :permalink "/r/TrueReddit/comments/4hyjkp/scientists_identify_common_thread_uniting_trump/",
                    :created_utc 1462425233,
                    :subwordpressdotcom "TrueReddit",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1017/xps.2014.1",
                    :id "4htc63",
                    :title "Full Transparency of Politicians' Actions Does Not Increase the Quality of Political Representation (research paper)",
                    :author "DaveSta123",
                    :permalink "/r/politics/comments/4htc63/full_transparency_of_politicians_actions_does_not/",
                    :created_utc 1462344548,
                    :subwordpressdotcom "politics",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1016/j.ebiom.2016.04.008",
                    :id "4g6krt",
                    :title "Systems Nutrigenomics Reveals Brain Gene Networks Linking Metabolic and Brain Disorders",
                    :author "carlsonbjj",
                    :permalink "/r/depressionregimens/comments/4g6krt/systems_nutrigenomics_reveals_brain_gene_networks/",
                    :created_utc 1461466887,
                    :subwordpressdotcom "depressionregimens",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1038/nphys3715",
                    :id "4g6af5",
                    :title "Coincidence of a high-fluence blazar outburst with a PeV-energy neutrino event",
                    :author "Bere96",
                    :permalink "/r/ParticlePhysics/comments/4g6af5/coincidence_of_a_highfluence_blazar_outburst_with/",
                    :created_utc 1461461589,
                    :subwordpressdotcom "ParticlePhysics",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1038/ncomms11239",
                    :id "4fml3g",
                    :title "The timescales of global surface-ocean connectivity. Planktonic communities are shaped through a balance of local evolutionary adaptation and ecological succession driven in large part by migration",
                    :author "ninthinning01",
                    :permalink "/r/science/comments/4fml3g/the_timescales_of_global_surfaceocean/",
                    :created_utc 1461145190,
                    :subwordpressdotcom "science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1017/S1537592715003205",
                    :id "4exapx",
                    :title "Wendy Pearlman's latest article on changing Narratives of Fear in Syria. From interviews with 200 Syrian refugees.",
                    :author "thinkaboutfun",
                    :permalink "/r/syriancivilwar/comments/4exapx/wendy_pearlmans_latest_article_on_changing/",
                    :created_utc 1460735047,
                    :subwordpressdotcom "syriancivilwar",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1371/journal.pone.0153104",
                    :id "4ep5hv",
                    :title "My group just published a De Novo Assembler comparison paper. We'd appreciate any feedback, and we'd love to answer any questions!",
                    :author "Blaze9",
                    :permalink "/r/bioinformatics/comments/4ep5hv/my_group_just_published_a_de_novo_assembler/",
                    :created_utc 1460601724,
                    :subwordpressdotcom "bioinformatics",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1371/journal.pone.0152136",
                    :id "4en1oa",
                    :title "Technological Analysis of the World’s Earliest Shamanic Costume",
                    :author "AdrianEvans",
                    :permalink "/r/Anthropology/comments/4en1oa/technological_analysis_of_the_worlds_earliest/",
                    :created_utc 1460573448,
                    :subwordpressdotcom "Anthropology",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1371/journal.pone.0152136",
                    :id "4emzr6",
                    :title "Technological Analysis of the World’s Earliest Shamanic Costume: Star Carr",
                    :author "AdrianEvans",
                    :permalink "/r/Archaeology/comments/4emzr6/technological_analysis_of_the_worlds_earliest/",
                    :created_utc 1460572787,
                    :subwordpressdotcom "Archaeology",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1063/1.4945018",
                    :id "4e7rg2",
                    :title "Anomalous results observed in magnetization of bulk high temperature superconductors—A windfall for applications",
                    :author "acidacetylsalicylic",
                    :permalink "/r/Physics/comments/4e7rg2/anomalous_results_observed_in_magnetization_of/",
                    :created_utc 1460324726,
                    :subwordpressdotcom "Physics",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1038/ncomms11148",
                    :id "4dtrhb",
                    :title "Evidence for ice-free summers in the late Miocene central Arctic Ocean",
                    :author "ninthinning01",
                    :permalink "/r/science/comments/4dtrhb/evidence_for_icefree_summers_in_the_late_miocene/",
                    :created_utc 1460071468,
                    :subwordpressdotcom "science",
                    :kind "t3"} 
                    {:url "http://dx.doi.org/10.1016/j.bpj.2016.01.029",
                    :id "4dl4nl",
                    :title "Diagonally Scanned Light-Sheet Microscopy for Fast Volumetric Imaging of Adherent Cells.",
                    :author "markkitt",
                    :permalink "/r/science/comments/4dl4nl/diagonally_scanned_lightsheet_microscopy_for_fast/",
                    :created_utc 1459937370,
                    :subwordpressdotcom "science",
                    :kind "t3"}]}
                    ]
                

              (is (= result expected)))))
